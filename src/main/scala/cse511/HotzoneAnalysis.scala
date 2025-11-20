
package cse511

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

object HotzoneAnalysis {
  def run(spark: SparkSession, pointCsv: String, rectCsv: String): DataFrame = {
    import spark.implicits._

    val points = spark.read.option("header", "false").text(pointCsv).toDF("point")
    val rects  = spark.read.option("header", "false").text(rectCsv).toDF("rectangle")

    val rectContains = udf((rect: String, pt: String) => HotzoneUtils.rectContainsPoint(rect, pt))

    val joined = points.crossJoin(rects)
      .filter(rectContains(col("rectangle"), col("point")))

    val counts = joined.groupBy("rectangle").count().withColumnRenamed("count", "hotness")
    counts.orderBy("rectangle")
  }
}
