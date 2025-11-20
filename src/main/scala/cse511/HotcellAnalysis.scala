
package cse511

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object HotcellAnalysis {

  /** Expected input CSV format: columns "pickup_datetime","pickup_longitude","pickup_latitude"
    * We'll parse: pickup_datetime in "yyyy-MM-dd HH:mm:ss", long/lat as doubles
    */
  def run(spark: SparkSession, tripsCsv: String): DataFrame = {
    import spark.implicits._

    val raw = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(tripsCsv)
      .select($"pickup_datetime", $"pickup_longitude", $"pickup_latitude")

    val toX = udf((lon: Double) => math.floor(lon / 0.01).toInt)
    val toY = udf((lat: Double) => math.floor(lat / 0.01).toInt)
    val toZ = udf((ts: String) => HotcellUtils.timeToStep(ts))

    val cells = raw
      .filter($"pickup_longitude".isNotNull && $"pickup_latitude".isNotNull && $"pickup_datetime".isNotNull)
      .withColumn("x", toX($"pickup_longitude"))
      .withColumn("y", toY($"pickup_latitude"))
      .withColumn("z", toZ($"pickup_datetime"))
      .groupBy("x","y","z").count().withColumnRenamed("count","w")

    // Compute neighborhood sums with 3x3x3 window
    val neigh = cells.as("a")
      .join(cells.as("b"),
        abs(col("a.x") - col("b.x")) <= 1 &&
        abs(col("a.y") - col("b.y")) <= 1 &&
        abs(col("a.z") - col("b.z")) <= 1, "left")
      .groupBy(col("a.x").as("x"), col("a.y").as("y"), col("a.z").as("z"), col("a.w").as("w"))
      .agg(
        sum(col("b.w")).as("sumW"),
        count(col("b.w")).as("nbr") // number of neighbors in window (<=27)
      )

    // Global stats
    val n = cells.count().toDouble
    val mean = cells.agg(avg("w")).first().getDouble(0)
    val variance = cells.agg(var_pop("w")).first().getDouble(0)
    val stddev = math.sqrt(variance)

    val broadcastMean = spark.sparkContext.broadcast(mean)
    val broadcastStd  = spark.sparkContext.broadcast(stddev)
    val broadcastN    = spark.sparkContext.broadcast(n)

    // Simplified Getis-Ord Gi* z-score approximation:
    // z = (sumW - mean * nbr) / (std * sqrt( (nbr * (27.0 - nbr)) / (n - 1.0) + nbr ))
    // Note: this is a pragmatic approximation for portfolio purposes.
    val zUdf = udf { (sumW: Double, nbr: Long) =>
      val m = broadcastMean.value
      val s = broadcastStd.value
      val N = broadcastN.value
      val nb = nbr.toDouble
      if (s == 0.0 || N <= 1.0) 0.0
      else {
        val numer = sumW - m * nb
        val denom = s * math.sqrt(nb)
        if (denom == 0.0) 0.0 else numer / denom
      }
    }

    val scored = neigh.withColumn("zscore", zUdf(col("sumW").cast("double"), col("nbr")))

    // Sort by z-score desc and output top 50 as coords only
    val top = scored.orderBy(col("zscore").desc).limit(50).select("x","y","z")
    top
  }
}
