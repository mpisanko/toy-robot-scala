package rea.robot

import Position.Coordinates

case class Robot(position: Position = NotPlaced, maxCoordinates: Coordinates = Coordinates(4, 4)) {
  def isPositionValid(position: Coordinates): Boolean = position.within(maxCoordinates)
}
