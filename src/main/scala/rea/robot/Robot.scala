package rea.robot

import Position.Coordinates

case class Robot(position: Position = NotPlaced, maxCoordinates: Coordinates = Coordinates(4, 4)) {
  def isPositionValid(position: Coordinates): Boolean = position.within(maxCoordinates)
  def execute(maybeCommand: Option[Command]): Robot = maybeCommand match {
    case Some(command) => doExecute(command)
    case None => this
  }
  def doExecute(command: Command): Robot = command match {
    case Left => this.copy()
  }
}
