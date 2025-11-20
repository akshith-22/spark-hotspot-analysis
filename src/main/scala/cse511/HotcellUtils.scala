
package cse511

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object HotcellUtils {
  val stepFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  /** Convert lon,lat to grid using 0.01 degree cell size */
  def xyToCell(lon: Double, lat: Double): (Int, Int) = {
    val x = math.floor(lon / 0.01).toInt
    val y = math.floor(lat / 0.01).toInt
    (x, y)
  }

  /** Extract day-of-month step z from pickup datetime string; clamp to 1..31 */
  def timeToStep(ts: String): Int = {
    try {
      val dt = LocalDateTime.parse(ts.trim, stepFormatter)
      val d = dt.getDayOfMonth
      math.max(1, math.min(d, 31))
    } catch {
      case _: Throwable =>
        1
    }
  }
}
