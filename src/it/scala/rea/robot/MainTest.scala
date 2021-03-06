package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import rea.robot.ConfigurationTest.setEnv

class MainTest extends FlatSpec with Matchers {
  import scala.util.{Right,Left}
  it should "print help" in {
    assertResult(()) {Main.main(Array("--help"))}
  }

  //  The following test cases depend on the commands.txt and setting appropriate table size (if robot is placed outside of table bounds - command is ignored)
  it should "output correct information" in {
    setEnv(Configuration.TABLE_BOUNDS, "5:5")
    setEnv(Configuration.INPUT_FILE, "commands.txt")

    Main.runRobot() match {
      case Right(robot) => {
        assert(robot.isPlaced)
        robot.reporter.reports.size shouldEqual 3
      }
      case Left(_) => fail("Expected Success")
    }

  }

  it should "output correct information with big table" in {
    setEnv(Configuration.TABLE_BOUNDS, "67:7")
    setEnv(Configuration.INPUT_FILE, "commands.txt")

    Main.runRobot() match {
      case Right(robot) => {
        assert(robot.isPlaced)
        robot.reporter.reports.size shouldEqual 4
      }
      case Left(_) => fail("Expected Success")
    }
  }

  it should "output nothing when nothing to report" in {
    setEnv(Configuration.TABLE_BOUNDS, "1:1")
    setEnv(Configuration.INPUT_FILE, "commands.txt")

    Main.runRobot() match {
      case Right(robot) => {
        assert(robot.isPlaced == false)
        robot.reporter.reports.size shouldEqual 0
      }
      case Left(_) => fail("Expected Success")
    }
  }

  it should "not bump into objects placed on the table" in {
    setEnv(Configuration.TABLE_BOUNDS, "5:5")
    setEnv(Configuration.INPUT_FILE, "commands-with-objects.txt")

    Main.runRobot() match {
      case Right(robot) => {
        robot.position shouldEqual (Placed(0, 0, East))
        robot.bounds.obstacles.size shouldEqual 2
        robot.reporter.reports.size shouldEqual 6
      }
      case Left(_) => fail("Expected Success")
    }
  }

}
