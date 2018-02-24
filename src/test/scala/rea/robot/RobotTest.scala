package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import Position.Coordinates

class RobotTest extends FlatSpec with Matchers {

  it should "execute valid commands" in {
    val robot = Robot(Placed(0,0, West), Coordinates(1, 1), new StringListReporter)
    robot.execute(Left).position shouldEqual Placed(0,0, South)
    robot.execute(Right).position shouldEqual Placed(0,0, North)
    robot.execute(Report).reporter.asInstanceOf[StringListReporter].reports.size shouldEqual 1
    robot.execute(Place(1, 1, West)).position shouldEqual Placed(1, 1, West)
  }

  it should "ignore invalid commands" in {
    val robot = Robot(Placed(0,0, West), Coordinates(1, 1))
    robot.execute(Move) shouldEqual robot
    robot.execute(Place(NotPlaced)) shouldEqual robot
    robot.execute(Place(Placed(2,2, North))) shouldEqual robot
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
    placedRobot.position shouldEqual Placed(0,0,North)
    val movedRobot = placedRobot.execute(Move)
    movedRobot.position shouldEqual Placed(0,1,North)
    movedRobot.execute(Move).position shouldEqual movedRobot.position
    movedRobot.execute(Right).position shouldEqual Placed(0,1,East)
  }

  it should "process commands" in {
    val robot = Robot(NotPlaced, Coordinates(4, 4), new StringListReporter)
    val commands: TraversableOnce[String] =
      List("place -1,9,east", "report", "move",
          "place 0,0,north", "move", "move", "left",
          "move",
          "right", "move", "move", "report", "right", "move", "move", "report").toIterator
    val newRobot = robot.processCommands(commands)
    newRobot.position shouldEqual Placed(2, 4, East)
    newRobot.reporter.asInstanceOf[StringListReporter].reports.size shouldEqual 2
  }
}
