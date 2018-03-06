package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import rea.robot.Robot.Bounds

class ConfigurationTest extends FlatSpec with Matchers {
  import ConfigurationTest.{setEnv, rmEnv}
  import scala.util.{Right,Left}

  it should "be able to select desired Reporter" in {
    setEnv(Configuration.REPORTER, Configuration.STRING_REPORTER)
    Configuration.configureReporter(Configuration()) match {
      case Right(conf) =>
        conf.reporter shouldEqual StringListReporter()
      case Left(err) => fail(s"Error found: $err")
    }

  }

  it should "use default reporter when specified not valid" in {
    setEnv(Configuration.REPORTER, "ArgReporter")
    Configuration.configureReporter(Configuration()) match {
      case Right(conf) => conf.reporter shouldEqual ConsoleReporter()
      case Left(err) => fail(s"Error found: $err")
    }
  }

  it should "use default ConsoleReporter if environment variable not set" in {
    rmEnv(Configuration.REPORTER)
    Configuration.configureReporter(Configuration()) match {
      case Right(conf) =>
        conf.reporter shouldEqual ConsoleReporter()
      case Left(err) => fail(s"Error found: $err")
    }
  }

  it should "parse table bounds correctly when set" in {
    setEnv(Configuration.TABLE_BOUNDS, "666:2")
    Configuration.configureBounds(Right(Configuration())) match {
      case Right(conf) =>
        assert(conf.bounds == Bounds(665, 1))
        assert(conf.messages.size == 1)
      case Left(err) => fail(s"Error found: $err")
    }
  }

  it should "set default table bounds when not set" in {
    rmEnv(Configuration.TABLE_BOUNDS)
    Configuration.configureBounds(Right(Configuration())) match {
      case Right(conf) =>
        assert(conf.bounds == Bounds(4, 4))
        assert(conf.messages.size == 1)
      case Left(err) => fail(s"Error found: $err")
    }
  }

  it should "report error when bounds set incorrectly" in {
    setEnv(Configuration.TABLE_BOUNDS, "666-2")
    Configuration.configureBounds(Right(Configuration())) match {
      case Right(_) => fail("Should be error")
      case Left(err) => err shouldEqual "Table size must be specified as Width:Height (eg: 4:4) - both greater than zero, but was: 666-2"
    }
  }

  it should "report error when bounds set to non-positive value" in {
    setEnv(Configuration.TABLE_BOUNDS, "0:2")
    Configuration.configureBounds(Right(Configuration())) match {
      case Right(_) => fail("Should be error")
      case Left(err) => err shouldEqual "Table size must be specified as Width:Height (eg: 4:4) - both greater than zero, but was: 0:2"
    }
  }

  it should "configure input source correctly" in {
    rmEnv(Configuration.TABLE_BOUNDS)
    setEnv(Configuration.INPUT_FILE, "commands.txt")
    Configuration.configureInputSource(Right(Configuration())) match {
      case Right(conf) =>
        assert(conf.messages.size == 1)
        assert(conf.source.getLines().nonEmpty)
      case Left(err) => fail(s"Error found: $err")
    }
  }

  it should "use STDIN when input file not present" in {
    rmEnv(Configuration.TABLE_BOUNDS)
    setEnv(Configuration.INPUT_FILE, "__not-there__")
    Configuration.configureInputSource(Right(Configuration())) match {
      case Right(conf) => conf.messages.size == 2
      case Left(err) => fail(s"Error found: $err")
    }
  }

  it should "configure input source to STDIN when file not specified" in {
    rmEnv(Configuration.TABLE_BOUNDS)
    rmEnv(Configuration.INPUT_FILE)
    Configuration.configureInputSource(Right(Configuration())) match {
      case Right(conf) => assert(conf.messages.head == "Please input commands (command<ENTER>). To end press CTRL+D.")
      case Left(err) => fail(s"Error found: $err")
    }
  }

  it should "configure robot successfully" in {
    setEnv(Configuration.REPORTER, Configuration.STRING_REPORTER)
    setEnv(Configuration.TABLE_BOUNDS, "666:667")
    setEnv(Configuration.INPUT_FILE, "commands.txt")
    Configuration.buildRobotsConfiguration(Configuration()) match {
      case Right(conf) =>
        assert(conf.messages.size == 2)
        conf.reporter shouldEqual StringListReporter()
        assert(conf.bounds == Bounds(665, 666))
        assert(conf.source.getLines.nonEmpty)
      case Left(err) => fail(s"Error found: $err")
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

