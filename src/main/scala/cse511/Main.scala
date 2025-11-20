
package cse511

import org.apache.spark.sql.SparkSession

object Main {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: spark-submit <jar> <outputPath> <task> [task-args...]")
      System.err.println("Tasks: hotzoneanalysis <points.csv> <rectangles.csv> | hotcellanalysis <monthly_taxi_points.csv>")
      System.exit(1)
    }

    val outputPath = args(0)
    val task = args(1)

    val spark = SparkSession.builder().appName("Spark Hotspot Analysis").getOrCreate()

    task.toLowerCase() match {
      case "hotzoneanalysis" =>
        val points = args(2)
        val rects  = args(3)
        val df = HotzoneAnalysis.run(spark, points, rects)
        df.orderBy("rectangle").write.mode("overwrite").csv(s"$outputPath/hotzone")

      case "hotcellanalysis" =>
        val trips = args(2)
        val df = HotcellAnalysis.run(spark, trips)
        // The template outputs top 50 coords sorted by G*, but does not include G* in output
        // We write final coordinates only.
        df.write.mode("overwrite").csv(s"$outputPath/hotcell")

      case other =>
        System.err.println(s"Unknown task: $other")
        System.exit(2)
    }

    spark.stop()
  }
}
