
# Spark Hotspot Analysis (Scala)

Portfolio-ready implementation of **Hot Zone** and **Hot Cell** analysis on NYC taxi pickup data using Apache Spark.

- **Hot Zone:** range-join points with rectangles and count points per rectangle; output sorted by rectangle string.
- **Hot Cell:** compute **Getis-Ord Gi***-style z-scores on a 3D grid (lon, lat, day-of-month) and return the top 50 coordinates (no z-scores in output).

Project spec inspired by CSE 511 Assignment 3 (Fall 2025) fileciteturn2file0.

## Structure
```
spark-hotspot-analysis/
├── src/main/scala/cse511/
│   ├── Main.scala                # CLI entrypoint (hotzoneanalysis | hotcellanalysis)
│   ├── HotzoneAnalysis.scala     # range join + counts
│   ├── HotzoneUtils.scala
│   ├── HotcellAnalysis.scala     # 3D Gi*-style scoring (portfolio approximation)
│   └── HotcellUtils.scala
├── build.sbt
├── scripts/
│   ├── run_spark.sh              # spark-submit wrapper
│   └── dev_run.sh                # builds and runs samples
├── data/
│   ├── point-hotzone-sample.csv
│   ├── zone-hotzone-sample.csv
│   └── yellow_tripdata_2009-01_point_sample.csv
├── docs/
│   └── assignment_brief.pdf
└── README.md
```

## Build
```bash
sbt assembly
```
Jar output:
```
target/scala-2.12/spark-hotspot-analysis-assembly-0.1.0.jar
```

## Run (samples)
```bash
# Hot Zone
bash scripts/run_spark.sh result hotzoneanalysis data/point-hotzone-sample.csv data/zone-hotzone-sample.csv

# Hot Cell
bash scripts/run_spark.sh result hotcellanalysis data/yellow_tripdata_2009-01_point_sample.csv
```

Outputs appear under `result/hotzone` and `result/hotcell` respectively.

## Notes
- Cell size: 0.01° × 0.01° (lon/lat). Time step: day-of-month in [1..31].
- Boundary points are included (containment / within).
- Hot Cell scoring uses a pragmatic Gi*-style z-score focusing on portfolio readability.
