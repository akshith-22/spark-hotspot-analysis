
package cse511

object HotzoneUtils {
  /** Parse "x,y" to (x,y) as Double */
  def parsePoint(s: String): (Double, Double) = {
    val a = s.split(",").map(_.trim)
    (a(0).toDouble, a(1).toDouble)
  }

  /** Parse "x1,y1,x2,y2" to normalized (minX, minY, maxX, maxY) */
  def parseRect(s: String): (Double, Double, Double, Double) = {
    val a = s.split(",").map(_.trim).map(_.toDouble)
    val x1 = a(0); val y1 = a(1); val x2 = a(2); val y2 = a(3)
    (math.min(x1, x2), math.min(y1, y2), math.max(x1, x2), math.max(y1, y2))
  }

  /** True if rectangle contains point (boundary-inclusive) */
  def rectContainsPoint(rect: String, point: String): Boolean = {
    val (px, py) = parsePoint(point)
    val (minX, minY, maxX, maxY) = parseRect(rect)
    px >= minX && px <= maxX && py >= minY && py <= maxY
  }
}
