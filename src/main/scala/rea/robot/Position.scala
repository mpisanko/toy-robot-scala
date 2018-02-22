package rea.robot
import Position.Coordinates

sealed trait Position {
  def left: Position
  def right: Position
  def move: Position
  def place(newPosition: Placed): Position = newPosition
  def isPlaced: Boolean
}
final case object NotPlaced extends Position {
  def left = this
  def right = this
  def move = this
  val isPlaced: Boolean = false
}
final case class Placed(coordinates: Coordinates, direction: Direction) extends Position {
  def left = this.copy(direction = direction.left)
  def right = this.copy(direction = direction.right)
  def move = this.copy(coordinates = coordinates.move(direction))
  val isPlaced: Boolean = true
}

object Position {

  /**
    * Zero based Coordinates
    */
  case class Coordinates(x: Int, y: Int) {
    def within(other: Coordinates) = 0 <= x && 0 <= y && x <= other.x && y <= other.y
    def encloses(other: Coordinates) = 0 <= other.x && 0 <= other.y && x >= other.x && y >= other.y
    def move(direction: Direction): Coordinates = direction match {
      case North => this.copy(x, y + 1)
      case South => this.copy(x, y - 1)
      case East => this.copy(x + 1, y)
      case West => this.copy(x - 1, y)
    }
  }
}

object Placed {
  def apply(x: Int, y: Int, direction: Direction): Placed = Placed(Coordinates(x, y), direction)

}
