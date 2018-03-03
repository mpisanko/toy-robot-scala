package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import rea.robot.ConfigurationTest.setEnv

class MainTest extends FlatSpec with Matchers {
  import MainTest.isReportCount

  it should "print help" in {
    assertResult(()) {Main.main(Array("--help"))}
  }

  //  The following test cases depend on the commands.txt and setting appropriate table size (if robot is placed outside of table bounds - command is ignored)
  it should "output correct information" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    setEnv(Configuration.TABLE_BOUNDS, "5:5")
    setEnv(Configuration.INPUT_FILE, "commands.txt")

    val robot = Main.runRobot()
    assert(robot.isPlaced)
    assert(isReportCount(robot, 3))
  }

  it should "output correct information with big table" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    setEnv(Configuration.TABLE_BOUNDS, "67:7")
    setEnv(Configuration.INPUT_FILE, "commands.txt")

    val robot = Main.runRobot()
    assert(robot.isPlaced)
    isReportCount(robot, 4)
  }

  it should "output nothing when nothing to report" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    setEnv(Configuration.TABLE_BOUNDS, "1:1")
    setEnv(Configuration.INPUT_FILE, "commands.txt")

    val robot = Main.runRobot()
    assert(robot.isPlaced == false)
    isReportCount(robot, 0)
  }

  it should "not bump into objects placed on the table" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    setEnv(Configuration.TABLE_BOUNDS, "5:5")
    setEnv(Configuration.INPUT_FILE, "commands-with-objects.txt")

    val robot = Main.runRobot()
    robot.position shouldEqual(Placed(0, 0, East))
    robot.bounds.obstacles.size shouldEqual 2
    isReportCount(robot, 3)
  }

}

object MainTest {
  def isReportCount(robot: Robot, lines: Int): Boolean = {
    robot.reporter.asInstanceOf[StringListReporter].reports.size == lines
  }
}