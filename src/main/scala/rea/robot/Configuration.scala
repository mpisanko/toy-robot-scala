package rea.robot

import java.io.FileNotFoundException

import rea.robot.Robot.Bounds

import scala.io.BufferedSource
import scala.util.matching.Regex

object Configuration {

  type Errors = List[String]
  type Messages = List[String]

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
  val buildRobotsConfiguration: ((Messages, Errors)) => ((Messages, Errors, Reporter, Bounds, BufferedSource)) =
    configureReporter _ andThen configureBounds _ andThen configureInputSource _

  def configureReporter(args: (Messages, Errors)): (Messages, Errors, Reporter) = try {
    (s"Reporter: ${getEnv(REPORTER_CLASS, DEFAULT_REPORTER_CLASS)}" :: args._1, args._2,
      Class.forName(getEnv(REPORTER_CLASS, DEFAULT_REPORTER_CLASS)).newInstance.asInstanceOf[Reporter])
  } catch {
    case _: ClassNotFoundException => (args._1, s"Class '${getEnv(REPORTER_CLASS, "")}' not found." :: args._2,
      new ConsoleReporter)
    case _: Throwable => (args._1, s"Problem instantiating class '${getEnv(REPORTER_CLASS, "")}'." :: args._2,
      new ConsoleReporter)
  }

  val tableBoundsRegex: Regex = """(\d+):(\d+)""".r
  /**
    * Coordinates are zero based so we need to subtract 1 from desired size.
    * @return
    */
  def configureBounds(args: (Messages, Errors, Reporter)): (Messages, Errors, Reporter, Bounds) =
    getEnv(TABLE_BOUNDS, DEFAULT_TABLE_BOUNDS) match {
      case tableBoundsRegex(x, y) =>
        val (maxX, maxY) = (x.toInt - 1, y.toInt - 1)
        if (maxX >= 0 && maxY >= 0) {
          (s"Table size: ${maxX + 1} by ${maxY + 1}" :: args._1, args._2, args._3, Bounds(maxX, maxY))
        } else {
          (args._1, s"Table size must be at least 1x1, but was: ${x}x$y)" :: args._2, args._3, Bounds(maxX, maxY))
        }
      case _ =>
        (args._1, s"Table size must be specified as Width:Height (eg: 4:4), but was: ${getEnv(TABLE_BOUNDS, "")})" :: args._2, args._3, Bounds(4, 4))
    }

  def configureInputSource(args: (Messages, Errors, Reporter, Bounds)): (Messages, Errors, Reporter, Bounds, BufferedSource) =
    getEnv(INPUT_FILE).map(fileName => try {
      (s"Using file $fileName as input." :: args._1, args._2, args._3, args._4, io.Source.fromFile(fileName))
    } catch {
      case _: FileNotFoundException => (args._1, s"File $fileName not found." :: args._2, args._3, args._4, io.Source.stdin)
      case _: Throwable => (args._1, s"Encountered a problem reading file: $fileName" :: args._2, args._3, args._4, io.Source.stdin)
    }).getOrElse ("Please input commands (command<ENTER>). To end press CTRL+D." :: args._1, args._2,
      args._3, args._4, io.Source.stdin)

  def getEnv(key: String, default: String): String = {
    scala.util.Properties.envOrElse(key, default)
  }

  def getEnv(key: String): Option[String] = {
    scala.util.Properties.envOrNone(key)
  }

}
