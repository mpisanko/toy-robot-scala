package rea.robot

import Position.Coordinates

case class Robot(position: Position = NotPlaced, bounds: Coordinates = Coordinates(4, 4), reporter: Reporter = new ConsoleReporter) {

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
      case Report => position match {
        case Placed(coordinates, direction) => reporter.report(coordinates, direction); this
        case _ => this
      }
      case _ => this
    } else this

  /**
    * Was this Robot Placed?
    * @return whether a valid Place command was issued
    */
  def isPlaced: Boolean = position.isPlaced

  /**
    * Command is valid if it is a Place command with coords within bounds or any other command when robot is placed
    * @param command
    * @return whether this command should be executed (or ignored)
    */
  private def isCommandValid(command: Command): Boolean = command match {
    case Place(pos) => isPositionValid(pos)
    case Move => isPlaced && isPositionValid(position.move)
    case _ => isPlaced
  }

  private def isPositionValid(position: Position): Boolean = position match {
    case Placed(coords, _) => coords.within(bounds)
    case _ => false
  }
}
