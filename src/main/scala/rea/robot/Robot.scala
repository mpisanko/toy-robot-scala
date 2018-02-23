package rea.robot

import Position.Coordinates

case class Robot(position: Position = NotPlaced, bounds: Coordinates = Coordinates(4, 4), reporter: Reporter = new ConsoleReporter) {
  /**
    * Command is valid if it is a Place command with coords within bounds or any other command when robot is placed
    * @param command
    * @return whether this command should be executed (or ignored)
    */
  def isCommandValid(command: Command): Boolean = command match {
    case Place(pos) => isPositionValid(pos)
    case Move => isPlaced && isPositionValid(position.move)
    case _ => isPlaced
  }
  def isPositionValid(position: Position): Boolean = position match {
    case Placed(coords, _) => coords.within(bounds)
    case _ => false
  }

  def execute(command: Command): Robot =
    if (isCommandValid(command)) command match {
      case Left => this.copy(position = position.left)
      case Right => this.copy(position = position.right)
      case Move => this.copy(position = position.move)
      case Place(newPosition: Placed) => this.copy(position = position.place(newPosition))
      case Report => position match {
        case Placed(coordinates, direction) => reporter.report(coordinates, direction); this
        case _ => this
      }
      case _ => this
    } else this

  def isPlaced: Boolean = position.isPlaced
}
