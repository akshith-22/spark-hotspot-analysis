
#!/bin/bash
# scripts/run_spark.sh
# Example:
#   bash scripts/run_spark.sh result hotzoneanalysis data/point-hotzone-sample.csv data/zone-hotzone-sample.csv
#   bash scripts/run_spark.sh result hotcellanalysis data/yellow_tripdata_2009-01_point_sample.csv

set -e

ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
JAR="$ROOT_DIR/target/scala-2.12/spark-hotspot-analysis-assembly-0.1.0.jar"

if [ ! -f "$JAR" ]; then
  echo "Assembly jar not found. Building with sbt assembly..."
  sbt assembly
fi

spark-submit "$JAR" "$@"
