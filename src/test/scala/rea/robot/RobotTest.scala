package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import Position.Coordinates

class RobotTest extends FlatSpec with Matchers {
  it should "correctly establish validity of position" in {
    val robot = Robot(NotPlaced, Coordinates(3, 3))
    assert(robot.isPositionValid(Placed(0, 0, North)))
    assert(robot.isPositionValid(Placed(3, 3, East)))
    assert(robot.isPositionValid(Placed(3, 1, West)))

    robot.isPositionValid(Placed(3, -1, South)) shouldEqual false
    robot.isPositionValid(Placed(4, 2, North)) shouldEqual false
    robot.isPositionValid(NotPlaced) shouldEqual false
  }

  it should "detect valid commands" in {
    val robot = Robot(Placed(0,0, West), Coordinates(1, 1))
    assert(robot.isCommandValid(Left))
    assert(robot.isCommandValid(Right))
    assert(robot.isCommandValid(Report))
    assert(robot.isCommandValid(Place(1, 1, West)))
  }

  it should "detect invalid commands" in {
    val robot = Robot(Placed(0,0, West), Coordinates(1, 1))
    robot.isCommandValid(Move) shouldEqual false
    robot.isCommandValid(Place(NotPlaced)) shouldEqual false
    robot.isCommandValid(Place(Placed(2,2, North))) shouldEqual false
    }
  }
