package rea.robot

sealed trait Direction {
  def left: Direction
  def right: Direction
}
final case object North extends Direction {
  override def left: Direction = West
  override def right: Direction = East
}
final case object South extends Direction {
  override def left: Direction = East
  override def right: Direction = West
}
final case object East extends Direction {
  override def left: Direction = North
  override def right: Direction = South
}
final case object West extends Direction {
  override def left: Direction = South
  override def right: Direction = North
}

object Direction {
  def parse(dir: String): Option[Direction] = dir.trim.toUpperCase match {
    case "NORTH" => Some(North)
    case "SOUTH" => Some(South)
    case "EAST"  => Some(East)
    case "WEST"  => Some(West)
    case _       => None
  }
}
