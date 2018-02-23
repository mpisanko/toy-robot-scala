package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import rea.robot.Position.Coordinates

import scala.collection.mutable

class ConfigurationTest extends FlatSpec with Matchers {
  import ConfigurationTest.{setEnv, rmEnv}
  it should "be able to select desired Reporter" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    val (msgs, errs, reporter) = Configuration.configureReporter(List(), List())
    assert(msgs.size == 1)
    assert(errs.isEmpty)
    assert(reporter.getClass == classOf[StringListReporter])
  }

  it should "report error when reporter class does not exist" in {
    setEnv(Configuration.REPORTER_CLASS, "NoSuchClass")
    val (msgs, errs, _) = Configuration.configureReporter(List(), List())
    assert(errs.size == 1)
    assert(msgs.isEmpty)
  }

  it should "report error when reporter class cannot be instantiated" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.ArgReporter")
    val (msgs, errs, _) = Configuration.configureReporter(List(), List())
    assert(errs.size == 1)
    assert(msgs.isEmpty)
  }

  it should "use default ConsoleReporter if environment variable not set" in {
    rmEnv(Configuration.REPORTER_CLASS)
    val (msgs, errs, reporter) = Configuration.configureReporter(List(), List())
    assert(msgs.size == 1)
    assert(errs.isEmpty)
    assert(reporter.getClass == classOf[ConsoleReporter])
  }

  it should "parse table bounds correctly when set" in {
    setEnv(Configuration.TABLE_BOUNDS, "666:2")
    val (msgs, errs, _, bounds) = Configuration.configureBounds(List(), List(), new ConsoleReporter)
    assert(errs.isEmpty)
    assert(msgs.size == 1)
    assert(bounds == Coordinates(665, 1))
  }

  it should "set default table bounds when not set" in {
    rmEnv(Configuration.TABLE_BOUNDS)
    val (msgs, errs, _, bounds) = Configuration.configureBounds(List(), List(), new ConsoleReporter)
    assert(errs.isEmpty)
    assert(msgs.size == 1)
    assert(bounds == Coordinates(4, 4))
  }

  it should "report error when bounds set incorrectly" in {
    setEnv(Configuration.TABLE_BOUNDS, "666-2")
    val (msgs, errs, _, _) = Configuration.configureBounds(List(), List(), new ConsoleReporter)
    assert(msgs.isEmpty)
    assert(errs.size == 1)
  }

  it should "report error when bounds set to non-positive value" in {
    setEnv(Configuration.TABLE_BOUNDS, "0:2")
    val (msgs, errs, _, _) = Configuration.configureBounds(List(), List(), new ConsoleReporter)
    assert(msgs.isEmpty)
    assert(errs.head == "Table size must be at least 1x1, but was: 0x2)")
  }

  it should "configure input source correctly" in {
    setEnv(Configuration.INPUT_FILE, "commands.txt")
    val (msgs, errs, _, _, input) = Configuration.configureInputSource(List(), List(), new ConsoleReporter, Coordinates(4, 4))
    assert(msgs.size == 1)
    assert(errs.isEmpty)
    assert(input.getLines().nonEmpty)
  }

  it should "report error when input source incorrect" in {
    setEnv(Configuration.INPUT_FILE, "__not-there__")
    val (msgs, errs, _, _, input) = Configuration.configureInputSource(List(), List(), new ConsoleReporter, Coordinates(4, 4))
    assert(errs.size == 1)
    assert(msgs.isEmpty)
  }

  it should "configure input source to STDIN when file not specified" in {
    rmEnv(Configuration.INPUT_FILE)
    val (msgs, errs, _, _, _) = Configuration.configureInputSource(List(), List(), new ConsoleReporter, Coordinates(4, 4))
    assert(msgs.head == "Please input commands (command<ENTER>). To end press CTRL+D.")
    assert(errs.isEmpty)
  }

  it should "configure robot successfully" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    setEnv(Configuration.TABLE_BOUNDS, "666:667")
    setEnv(Configuration.INPUT_FILE, "commands.txt")
    val (msgs, errs, reporter, bounds, input) = Configuration.configureRobot(List(), List())
    assert(errs.isEmpty)
    assert(msgs.size == 3)
    assert(reporter.getClass == classOf[StringListReporter])
    assert(bounds == Coordinates(665, 666))
    assert(input.getLines.nonEmpty)
  }

}

object ConfigurationTest {
  def setEnv(key: String, value: String) = {
    enabledEnvMap.put(key, value)
  }
  def rmEnv(key: String) = {
    enabledEnvMap.remove(key)
  }
  private def enabledEnvMap: java.util.Map[String, String] = {
    val field = System.getenv().getClass.getDeclaredField("m")
    field.setAccessible(true)
    field.get(System.getenv()).asInstanceOf[java.util.Map[java.lang.String, java.lang.String]]
  }
}

class ArgReporter(ignoreMe: String) extends Reporter {
  override def report(coordinates: Position.Coordinates, direction: Direction): Unit = {}
}
