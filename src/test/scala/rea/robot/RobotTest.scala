package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import Position.Coordinates
import rea.robot.Robot.Bounds

class RobotTest extends FlatSpec with Matchers {

  it should "execute valid commands" in {
    val robot = Robot(Placed(0,0, West), Bounds(1, 1), StringListReporter())
    robot.execute(Left).position shouldEqual Placed(0,0, South)
    robot.execute(Right).position shouldEqual Placed(0,0, North)
    robot.execute(Report).reporter.reports.size shouldEqual 1
    robot.execute(Place(1, 1, West)).position shouldEqual Placed(1, 1, West)
  }

  it should "ignore invalid commands" in {
    val robot = Robot(Placed(0,0, West), Bounds(1, 1))
    robot.execute(Move) shouldEqual robot
    robot.execute(Place(NotPlaced)) shouldEqual robot
    robot.execute(Place(Placed(2,2, North))) shouldEqual robot
    }

  it should "ignore movement commands before being placed" in {
    val robot = Robot(NotPlaced, Bounds(1, 1))
    robot.execute(Left).position shouldEqual robot.position
    robot.execute(Move).position shouldEqual robot.position
    robot.execute(Right).position shouldEqual robot.position
  }

  it should "accept place command if within table bounds" in {
    val robot = Robot(NotPlaced, Bounds(1, 1))
    val placedRobot = robot.execute(Place(0,0, North))
    placedRobot.position shouldEqual Placed(0,0,North)
    val movedRobot = placedRobot.execute(Move)
    movedRobot.position shouldEqual Placed(0,1,North)
    movedRobot.execute(Move).position shouldEqual movedRobot.position
    movedRobot.execute(Right).position shouldEqual Placed(0,1,East)
  }

  it should "process commands" in {
    val robot = Robot(NotPlaced, Bounds(4, 4), StringListReporter())
    val commands: TraversableOnce[String] =
      List("place -1,9,east", "report", "move",
          "place 0,0,north", "move", "move", "left",
          "move",
          "right", "move", "move", "report", "right", "move", "move", "report").toIterator
    val newRobot = robot.processCommands(commands)
    newRobot.position shouldEqual Placed(2, 4, East)
    println(s"REports: ${newRobot.reporter.reports}")
    newRobot.reporter.reports.size shouldEqual 2
  }

  it should "ignore place object commands when not placed or object 'falls outside the table'" in {
    assert(Robot(NotPlaced).execute(PlaceObject).bounds.obstacles.isEmpty)
    assert(Robot(Placed(4,4, North), Bounds(4, 4)).execute(PlaceObject).bounds.obstacles.isEmpty)
  }

  it should "place objects when placed (and object falls within bounds)" in {
    assert(Robot(Placed(0,0, East), Bounds(4, 4)).execute(PlaceObject).bounds.obstacles.contains(Coordinates(1, 0)))
    assert(Robot(Placed(1,2, South), Bounds(4, 4)).execute(PlaceObject).bounds.obstacles.contains(Coordinates(1,1)))
    assert(Robot(Placed(3,3, West), Bounds(4, 4)).execute(PlaceObject).bounds.obstacles.contains(Coordinates(2,3)))
  }

  it should "not bump into placed objects" in {
    val bounds = Bounds(4, 4, Set(Coordinates(0, 0), Coordinates(4, 4)))
    Robot(Placed(1, 0, West), bounds).execute(Move).position shouldEqual(Placed(1, 0, West))
    Robot(Placed(4, 3, North), bounds).execute(Move).position shouldEqual(Placed(4, 3, North))
  }
}
