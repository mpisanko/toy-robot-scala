package rea.robot

case class Robot(maxX: Int = 5, maxY: Int = 5) {
  def isPositionValid(x: Int, y: Int): Boolean = 0 <= x && x <= maxX && 0 <= y && y <= maxY
}
