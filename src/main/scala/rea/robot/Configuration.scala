package rea.robot

import java.io.FileNotFoundException

import rea.robot.Robot.Bounds

import scala.io.BufferedSource
import scala.util.matching.Regex

sealed trait Configuration
final case class RobotConfiguration(reporter: Reporter = new ConsoleReporter(),
                                    bounds: Bounds = Bounds(4, 4),
                                    source: BufferedSource = io.Source.stdin,
                                    messages: Vector[String] = Vector.empty) extends Configuration {
  def addMessage(msg: String): RobotConfiguration = copy(messages = messages :+ msg)
}
final case class ConfError(message: String) extends Configuration

object Configuration {

  val REPORTER_CLASS = "REPORTER_CLASS"
  val INPUT_FILE = "INPUT_FILE"
  val TABLE_BOUNDS = "TABLE_BOUNDS"

  val DEFAULT_TABLE_BOUNDS = "5:5"
  val DEFAULT_REPORTER_CLASS = "rea.robot.ConsoleReporter"

  /**
    * This builds up robot's configuration from environment variables. Along the way accumulates messages and errors
    * that can be displayed to user.
    * This function takes
    */
  val buildRobotsConfiguration: (RobotConfiguration) => Configuration =
    configureReporter _ andThen configureBounds _ andThen configureInputSource _

  def configureReporter(conf: RobotConfiguration): Configuration = try {
    RobotConfiguration(reporter = Class.forName(getEnv(REPORTER_CLASS, DEFAULT_REPORTER_CLASS)).newInstance.asInstanceOf[Reporter])
  } catch {
    case _: ClassNotFoundException => ConfError(s"Class '${getEnv(REPORTER_CLASS, "")}' not found.")
    case _: Throwable => ConfError(s"Problem instantiating class '${getEnv(REPORTER_CLASS, "")}'.")

  }

  val tableBoundsRegex: Regex = """(\d+):(\d+)""".r
  /**
    * Coordinates are zero based so we need to subtract 1 from desired size.
    * @return
    */
  def configureBounds(conf: Configuration): Configuration = conf match {
    case rc: RobotConfiguration => getEnv(TABLE_BOUNDS, DEFAULT_TABLE_BOUNDS) match {
      case tableBoundsRegex(x, y) =>
        val (maxX, maxY) = (x.toInt - 1, y.toInt - 1)
        if (maxX >= 0 && maxY >= 0) {
          rc.copy(bounds = Bounds(maxX, maxY)).addMessage(s"Table size: $x by $y")
        } else {
          ConfError(s"Table size must be at least 1x1, but was: ${x}x$y")
        }
      case _ =>
        ConfError(s"Table size must be specified as Width:Height (eg: 4:4), but was: ${getEnv(TABLE_BOUNDS, "")}")
    }
    case err: ConfError => err
  }

  def configureInputSource(conf: Configuration): Configuration = conf match {
    case rc: RobotConfiguration =>
      getEnv(INPUT_FILE).map(fileName => try {
        rc.copy(source = io.Source.fromFile(fileName)).addMessage(s"Using file $fileName as input.")
      } catch {
        case _: FileNotFoundException => ConfError(s"File $fileName not found.")
        case _: Throwable => ConfError(s"Encountered a problem reading file: $fileName")
      }).getOrElse(rc.addMessage("Please input commands (command<ENTER>). To end press CTRL+D."))
    case err: ConfError => err
  }

  def getEnv(key: String, default: String): String = {
    scala.util.Properties.envOrElse(key, default)
  }

  def getEnv(key: String): Option[String] = {
    scala.util.Properties.envOrNone(key)
  }

}

