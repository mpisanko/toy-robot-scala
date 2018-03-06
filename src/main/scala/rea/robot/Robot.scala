package rea.robot

import Position.Coordinates
import rea.robot.Robot.Bounds

case class Robot(position: Position = NotPlaced, bounds: Bounds = Bounds(4, 4), reporter: Reporter = ConsoleReporter()) {

  /**
    * Reducer for the foldLeft operation (a Robot takes new command as input and returns resulting Robot)
    */
  val commandReducer: (Robot, String) => Robot = (robot: Robot, command: String) => robot.processCommand(command)

  /**
    * Process many raw commands
    * @param commands TraversableOnce of commands
    * @return Robot in final position
    */
  def processCommands(commands: TraversableOnce[String]): Robot = commands.foldLeft(this)(commandReducer(_, _))

  /**
    * Process a text version of command: delegate to Command to parse text, execute if successful
    * @param command raw textual command
    * @return resulting new Robot (in new position) or this
    */
  def processCommand(command: String): Robot = Command.parse(command).map(c => execute(c)).getOrElse(this)

  /**
    * Execute given Command if it is allowed and will not cause the Robot to fall off the disc
    * @param command Command to be executed
    * @return new Robot
    */
  def execute(command: Command): Robot =
    if (isCommandValid(command)) command match {
      case Left => this.copy(position = position.left)
      case Right => this.copy(position = position.right)
      case Move => this.copy(position = position.move)
      case Place(newPosition: Placed) => this.copy(position = position.place(newPosition))
      case Place(NotPlaced) => this
      case PlaceObject => this.copy(bounds = boundsWithObjectAt(position.move))
      case Report => position match {
        case Placed(coordinates, direction) => this.copy(reporter = reporter.addReport(formatReport(coordinates, direction)))
        case NotPlaced => this
      }
    } else this

  /**
    * Was this Robot Placed?
    * @return whether a valid Place command was issued
    */
  def isPlaced: Boolean = position.isPlaced

  /**
    * Add new object to current bounds at position newPosition
    * @param newPosition position to add bounds at
    * @return bounds with new obstacle
    */
  private def boundsWithObjectAt(newPosition: Position): Bounds = newPosition match {
    case Placed(newCoordinates, _) => bounds.copy(obstacles = (bounds.obstacles + newCoordinates))
    case NotPlaced => bounds
  }

  private def formatCoordinates(c: Coordinates): String = s"[${c.x},${c.y}]"
  private def formatReport(coordinates: Coordinates, direction: Direction): String =
    s"Current position is: ${coordinates.x}, ${coordinates.y}, ${direction.toString.toUpperCase}, " +
      s"obstacles: <${bounds.obstacles.map(formatCoordinates).mkString(",")}>"
  /**
    * Command is valid if it is a Place command with coords within bounds or any other command when robot is placed
    * @param command
    * @return whether this command should be executed (or ignored)
    */
  private def isCommandValid(command: Command): Boolean = command match {
    case Place(pos) => isPositionValid(pos)
    case Move => isPlaced && isPositionValid(position.move)
    case PlaceObject => isPlaced && isPositionValid(position.move)
    case Left => isPlaced
    case Right => isPlaced
    case Report => isPlaced
  }

  private def isPositionValid(position: Position): Boolean = bounds.positionValid(position)

}

object Robot {
  case class Bounds(maxCoordinates: Coordinates, obstacles: Set[Coordinates]) {
    def positionValid(position: Position): Boolean = position match {
      case Placed(coords, _) => coords.within(maxCoordinates) && !obstacles.contains(coords)
      case NotPlaced => false
    }
  }
  object Bounds {
    def apply(x: Int, y: Int, obstacles: Set[Coordinates] = Set.empty): Bounds = Bounds(Coordinates(x, y), obstacles)
  }
}