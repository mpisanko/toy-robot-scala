package rea.robot

import java.io.File

import rea.robot.Robot.Bounds

import scala.io.BufferedSource
import scala.util.matching.Regex

case class Configuration(reporter: Reporter = ConsoleReporter(),
                         bounds: Bounds = Bounds(4, 4),
                         source: BufferedSource = io.Source.stdin,
                         messages: Vector[String] = Vector.empty) {
  def addMessage(msg: String): Configuration = copy(messages = messages :+ msg)
}

object Configuration {
  import scala.util.{Either,Right,Left}

  val INPUT_FILE = "INPUT_FILE"
  val TABLE_BOUNDS = "TABLE_BOUNDS"
  val REPORTER = "REPORTER"

  val DEFAULT_TABLE_BOUNDS = "5:5"
  val CONSOLE_REPORTER = "CONSOLE"
  val STRING_REPORTER = "STRING"
  val DEFAULT_REPORTER = CONSOLE_REPORTER

  /**
    * This builds up robot's configuration from environment variables. Along the way accumulates messages and errors
    * that can be displayed to user.
    * This function takes
    */
  val buildRobotsConfiguration: (Configuration) => Either[String, Configuration] =
    configureReporter _ andThen configureBounds _ andThen configureInputSource _

  def configureReporter(conf: Configuration): Either[String, Configuration] =
    getEnv(REPORTER, DEFAULT_REPORTER).toUpperCase match {
      case STRING_REPORTER => Right(conf.copy(reporter = StringListReporter()))
      case CONSOLE_REPORTER => Right(conf.copy(reporter = ConsoleReporter()))
      case _ => Right(conf.copy(reporter = ConsoleReporter()))
    }

  val tableBoundsRegex: Regex = """([1-9][0-9]*):([1-9][0-9]*)""".r
  /**
    * Coordinates are zero based so we need to subtract 1 from desired size.
    * @return
    */
  def configureBounds(conf: Either[String, Configuration]): Either[String, Configuration] = conf match {
    case Left(err) => Left(err)
    case Right(c) => getEnv(TABLE_BOUNDS, DEFAULT_TABLE_BOUNDS) match {
      case tableBoundsRegex(x, y) =>
        val (maxX, maxY) = (x.toInt - 1, y.toInt - 1)
        Right(c.copy(bounds = Bounds(maxX, maxY)).addMessage(s"Table size: $x by $y"))
      case _ =>
        Left(s"Table size must be specified as Width:Height (eg: 4:4) - both greater than zero, but was: ${getEnv(TABLE_BOUNDS, "")}")
    }
  }

  def configureInputSource(conf: Either[String, Configuration]): Either[String, Configuration] = conf match {
    case Left(err) => Left(err)
    case Right(c) =>
      val fileName = getEnv(INPUT_FILE, "")
      if (new File(fileName).exists) {
        Right(c.copy(source = io.Source.fromFile(fileName)).addMessage(s"Using file $fileName as input."))
      } else {
        Right(c.addMessage("Please input commands (command<ENTER>). To end press CTRL+D."))
      }
    }

  def getEnv(key: String, default: String): String = {
    scala.util.Properties.envOrElse(key, default)
  }

  def getEnv(key: String): Option[String] = {
    scala.util.Properties.envOrNone(key)
  }

}

