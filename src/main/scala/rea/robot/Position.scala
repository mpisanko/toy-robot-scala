package rea.robot
import Position.Coordinates

sealed trait Position
final case object NotPlaced extends Position
final case class Placed(coordinates: Coordinates, direction: Direction) extends Position

object Position {

  /**
    * Zero based Coordinates
    */
  case class Coordinates(x: Int, y: Int) {
    def within(other: Coordinates) = 0 <= x && 0 <= y && x <= other.x && y <= other.y
    def encloses(other: Coordinates) = 0 <= other.x && 0 <= other.y && x >= other.x && y >= other.y
  }
}

object Placed {
  def apply(x: Int, y: Int, direction: Direction): Placed = Placed(Coordinates(x, y), direction)
}
