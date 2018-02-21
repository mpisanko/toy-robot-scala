package rea.robot

case class Robot(maxX: Int = 4, maxY: Int = 4) {
  def isPositionValid(x: Int, y: Int): Boolean = 0 <= x && x <= maxX && 0 <= y && y <= maxY
}
