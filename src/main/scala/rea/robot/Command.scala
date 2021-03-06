package rea.robot

import scala.util.matching.Regex

sealed trait Command
final case object Move extends Command
final case object Left extends Command
final case object Right extends Command
final case object Report extends Command
final case class Place(position: Position) extends Command
final case object PlaceObject extends Command

object Command {
  val placeCommandRegex: Regex = """\s*PLACE\s+(\d+)\s*,\s*(\d+)\s*,\s*(NORTH|SOUTH|EAST|WEST)\s*""".r
  def parse(input: String): Option[Command] = input.trim.toUpperCase match {
    case "MOVE" => Some(Move)
    case "RIGHT" => Some(Right)
    case "LEFT" => Some(Left)
    case "REPORT" => Some(Report)
    case "PLACE_OBJECT" => Some(PlaceObject)
    case placeCommandRegex(x, y, dir) => dir.trim.toUpperCase match {
      case "NORTH" => Some(Place(Placed(x.toInt, y.toInt, North)))
      case "SOUTH" => Some(Place(Placed(x.toInt, y.toInt, South)))
      case "EAST" => Some(Place(Placed(x.toInt, y.toInt, East)))
      case "WEST" => Some(Place(Placed(x.toInt, y.toInt, West)))
      case _ => None
    }
    case _ => None
  }
}

object Place {
  def apply(x: Int, y: Int, direction: Direction): Place = Place(Placed(x, y, direction))
}
