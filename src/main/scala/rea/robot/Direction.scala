package rea.robot

sealed trait Direction
final case object North extends Direction
final case object South extends Direction
final case object East extends Direction
final case object West extends Direction

object Direction {
  def apply(dir: String): Option[Direction] = dir.trim.toUpperCase match {
    case "NORTH" => Some(North)
    case "SOUTH" => Some(South)
    case "EAST"  => Some(East)
    case "WEST"  => Some(West)
    case _       => None
  }
}
