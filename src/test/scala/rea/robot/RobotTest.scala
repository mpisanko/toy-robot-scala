package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import Position.Coordinates

class RobotTest extends FlatSpec with Matchers {
  it should "correctly establish validity of position" in {
    val robot = Robot(NotPlaced, Coordinates(3, 3))
    robot.isPositionValid(Coordinates(0, 0)) shouldEqual true
    robot.isPositionValid(Coordinates(3, 3)) shouldEqual true
    robot.isPositionValid(Coordinates(3, 1)) shouldEqual true
    robot.isPositionValid(Coordinates(3, -1)) shouldEqual false
    robot.isPositionValid(Coordinates(4, 2)) shouldEqual false
  }
}
