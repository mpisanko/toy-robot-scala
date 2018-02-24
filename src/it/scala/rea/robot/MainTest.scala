package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import rea.robot.ConfigurationTest.setEnv

class MainTest extends FlatSpec with Matchers {
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
    val reporter = robot.reporter.asInstanceOf[StringListReporter]
    assert(reporter.reports.size == 3)
  }

  it should "output correct information with big table" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    setEnv(Configuration.TABLE_BOUNDS, "67:7")
    setEnv(Configuration.INPUT_FILE, "commands.txt")

    val robot = Main.runRobot()
    assert(robot.isPlaced)
    val reporter = robot.reporter.asInstanceOf[StringListReporter]
    assert(reporter.reports.size == 4)
  }

  it should "outputs nothing when nothing to report" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    setEnv(Configuration.TABLE_BOUNDS, "1:1")
    setEnv(Configuration.INPUT_FILE, "commands.txt")

    val robot = Main.runRobot()
    assert(robot.isPlaced == false)
    val reporter = robot.reporter.asInstanceOf[StringListReporter]
    assert(reporter.reports.isEmpty)
  }

}