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

  it should "ignore movement commands before being placed" in {
    val robot = Robot(NotPlaced, Coordinates(1, 1))
    robot.execute(Left).position shouldEqual robot.position
    robot.execute(Move).position shouldEqual robot.position
    robot.execute(Right).position shouldEqual robot.position
  }

  it should "accept place command if within table bounds" in {
    val robot = Robot(NotPlaced, Coordinates(1, 1))
    val placedRobot = robot.execute(Place(0,0, North))
    placedRobot.position shouldEqual Robot(Placed(0,0,North), Coordinates(1, 1)).position
    val movedRobot = placedRobot.execute(Move)
    movedRobot.position shouldEqual Robot(Placed(0,1,North), Coordinates(1, 1)).position
    movedRobot.execute(Move).position shouldEqual movedRobot.position
    movedRobot.execute(Right).position shouldEqual Robot(Placed(0,1,East), Coordinates(1, 1)).position
  }
}
