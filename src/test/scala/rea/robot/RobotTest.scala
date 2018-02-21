package rea.robot

import org.scalatest.{FlatSpec, Matchers}

class RobotTest extends FlatSpec with Matchers {
  it should "correctly establish validity of position" in {
    val robot = Robot(NotPlaced, 3, 3)
    robot.isPositionValid(0, 0) shouldEqual true
    robot.isPositionValid(3, 3) shouldEqual true
    robot.isPositionValid(3, 1) shouldEqual true
    robot.isPositionValid(3, -1) shouldEqual false
    robot.isPositionValid(4, 2) shouldEqual false
  }
}
