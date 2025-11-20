
#!/bin/bash
# scripts/dev_run.sh
set -e
ROOT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"

sbt assembly

# Hot zone analysis (sample)
spark-submit "$ROOT_DIR/target/scala-2.12/spark-hotspot-analysis-assembly-0.1.0.jar" "$ROOT_DIR/result"   hotzoneanalysis "$ROOT_DIR/data/point-hotzone-sample.csv" "$ROOT_DIR/data/zone-hotzone-sample.csv"

# Hot cell analysis (sample)
spark-submit "$ROOT_DIR/target/scala-2.12/spark-hotspot-analysis-assembly-0.1.0.jar" "$ROOT_DIR/result"   hotcellanalysis "$ROOT_DIR/data/yellow_tripdata_2009-01_point_sample.csv"
