package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import rea.robot.Robot.Bounds

class ConfigurationTest extends FlatSpec with Matchers {
  import ConfigurationTest.{setEnv, rmEnv}

  it should "be able to select desired Reporter" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    Configuration.configureReporter(RobotConfiguration()) match {
      case conf: RobotConfiguration =>
        assert(conf.reporter.getClass == classOf[StringListReporter])
      case err: ConfError => fail(s"Error found: ${err.message}")
    }

  }

  it should "report error when reporter class does not exist" in {
    setEnv(Configuration.REPORTER_CLASS, "NoSuchClass")
    Configuration.configureReporter(RobotConfiguration()) match {
      case _: RobotConfiguration => fail("Should be error")
      case err: ConfError => err.message shouldEqual "Class 'NoSuchClass' not found."
    }
  }

  it should "report error when reporter class cannot be instantiated" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.ArgReporter")
    Configuration.configureReporter(RobotConfiguration()) match {
      case _: RobotConfiguration => fail("Should be error")
      case err: ConfError => err.message shouldEqual "Problem instantiating class 'rea.robot.ArgReporter'."
    }
  }

  it should "use default ConsoleReporter if environment variable not set" in {
    rmEnv(Configuration.REPORTER_CLASS)
    Configuration.configureReporter(RobotConfiguration()) match {
      case conf: RobotConfiguration =>
        assert(conf.reporter.getClass == classOf[ConsoleReporter])
      case err: ConfError => fail(s"Error found: ${err.message}")
    }
  }

  it should "parse table bounds correctly when set" in {
    setEnv(Configuration.TABLE_BOUNDS, "666:2")
    Configuration.configureBounds(RobotConfiguration()) match {
      case conf: RobotConfiguration =>
        assert(conf.bounds == Bounds(665, 1))
        assert(conf.messages.size == 1)
      case err: ConfError => fail(s"Error found: ${err.message}")
    }
  }

  it should "set default table bounds when not set" in {
    rmEnv(Configuration.TABLE_BOUNDS)
    Configuration.configureBounds(RobotConfiguration()) match {
      case conf: RobotConfiguration =>
        assert(conf.bounds == Bounds(4, 4))
        assert(conf.messages.size == 1)
      case err: ConfError => fail(s"Error found: ${err.message}")
    }
  }

  it should "report error when bounds set incorrectly" in {
    setEnv(Configuration.TABLE_BOUNDS, "666-2")
    Configuration.configureBounds(RobotConfiguration()) match {
      case _: RobotConfiguration => fail("Should be error")
      case err: ConfError => err.message shouldEqual "Table size must be specified as Width:Height (eg: 4:4), but was: 666-2"
    }
  }

  it should "report error when bounds set to non-positive value" in {
    setEnv(Configuration.TABLE_BOUNDS, "0:2")
    Configuration.configureBounds(RobotConfiguration()) match {
      case _: RobotConfiguration => fail("Should be error")
      case err: ConfError => err.message shouldEqual "Table size must be at least 1x1, but was: 0x2"
    }
  }

  it should "configure input source correctly" in {
    setEnv(Configuration.INPUT_FILE, "commands.txt")
    Configuration.configureInputSource(RobotConfiguration(reporter = new ConsoleReporter, bounds = Bounds(4, 4))) match {
      case conf: RobotConfiguration =>
        assert(conf.messages.size == 1)
        assert(conf.source.getLines().nonEmpty)
      case err: ConfError => fail(s"Error found: ${err.message}")
    }
  }

  it should "report error when input source incorrect" in {
    setEnv(Configuration.INPUT_FILE, "__not-there__")
    Configuration.configureInputSource(RobotConfiguration(reporter = new ConsoleReporter, bounds = Bounds(4, 4))) match {
      case _: RobotConfiguration => fail("Should be error")
      case err: ConfError => err.message shouldEqual "File __not-there__ not found."
    }
  }

  it should "configure input source to STDIN when file not specified" in {
    rmEnv(Configuration.INPUT_FILE)
    Configuration.configureInputSource(RobotConfiguration(reporter = new ConsoleReporter, bounds = Bounds(4, 4))) match {
      case rc: RobotConfiguration => assert(rc.messages.head == "Please input commands (command<ENTER>). To end press CTRL+D.")
      case err: ConfError => fail(s"Error found: ${err.message}")
    }
  }

  it should "configure robot successfully" in {
    setEnv(Configuration.REPORTER_CLASS, "rea.robot.StringListReporter")
    setEnv(Configuration.TABLE_BOUNDS, "666:667")
    setEnv(Configuration.INPUT_FILE, "commands.txt")
    Configuration.buildRobotsConfiguration(RobotConfiguration()) match {
      case conf: RobotConfiguration =>
        assert(conf.messages.size == 2)
        assert(conf.reporter.getClass == classOf[StringListReporter])
        assert(conf.bounds == Bounds(665, 666))
        assert(conf.source.getLines.nonEmpty)
      case err: ConfError => fail(s"Error found: ${err.message}")
    }
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

/**
  * A class which cannot be instantiated w/o arguments - in order to test scenario of instantiation error
  * @param onlyHereToCauseError just to cause instantiation error
  */
class ArgReporter(onlyHereToCauseError: String) extends Reporter {
  type T = Unit
  override def report(): T = {}
}
