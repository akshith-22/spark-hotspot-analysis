
# Hotspot Analysis Data

The full NYC taxi point dataset is large and not included in this repository.

Download it from the link below and place the extracted CSV file in this `data/` directory:

https://www.dropbox.com/scl/fi/l2r5ouz1c3wq0ogeenbw4/nyc_yellow_trip_data_2009-01_point.csv.zip?rlkey=g7rn9rptxhtf3c2ffngngwmm0&e=1&dl=0

**Expected filename after unzip:**
- `nyc_yellow_trip_data_2009-01_point.csv`

**Run with the real data (example):**
```bash
bash scripts/run_spark.sh result hotcellanalysis data/nyc_yellow_trip_data_2009-01_point.csv
```
