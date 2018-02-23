package rea.robot

import java.io.FileNotFoundException

import Position.Coordinates

import scala.io.BufferedSource

object Main {

  type Errors = List[String]
  type Messages = List[String]

  val REPORTER_CLASS = "REPORTER_CLASS"
  val INPUT_FILE = "INPUT_FILE"
  val TABLE_BOUNDS = "TABLE_BOUNDS"

  def main(args: Array[String]): Unit = {
    args.toList match {
      case ("--help" :: Nil) => usage()
      case _ => doProcessing()
    }
  }

  val configureRobot = configureReporter _ andThen configureInputSource _ andThen configureBounds _

  def doProcessing(): Robot = {
    lazy val (messages, errors, reporter, input, tableBounds) = configureRobot((List(), List()))

    reportOrTerminateIfErrors(messages, errors)

    val robot = Robot(NotPlaced, tableBounds, reporter)
    withOpen(input)(processInput(robot))
  }

  def processInput(robot: Robot)(input: BufferedSource): Robot =
    input.getLines.map(Command.parse).flatten.foldLeft(robot)(reducer(_, _))

  def reducer(robot: Robot, command: Command): Robot = robot.execute(command)

  val DEFAULT_REPORTER_CLASS = "rea.robot.ConsoleReporter"
  def configureReporter(args: (Messages, Errors)): (Messages, Errors, Reporter) = try {
    (s"Reporter: ${getEnv(REPORTER_CLASS, DEFAULT_REPORTER_CLASS)}" :: args._1, args._2, Class.forName(getEnv(REPORTER_CLASS, DEFAULT_REPORTER_CLASS)).newInstance.asInstanceOf[Reporter])
  } catch {
    case _: ClassNotFoundException => (args._1, s"Class '${getEnv(REPORTER_CLASS, "")}' not found." :: args._2, new ConsoleReporter)
    case _: Throwable => (args._1, s"Problem instantiating class '${getEnv(REPORTER_CLASS, "")}'." :: args._2, new ConsoleReporter)
  }

  def configureInputSource(args: (Messages, Errors, Reporter)): (Messages, Errors, Reporter, BufferedSource) =
    getEnv(INPUT_FILE).map(fileName => try {
      (s"Using file $fileName as input." :: args._1, args._2, args._3, io.Source.fromFile(fileName))
    } catch {
      case _: FileNotFoundException => (args._1, s"File $fileName not found." :: args._2, args._3, io.Source.stdin)
      case _: Throwable => (args._1, s"Encountered a problem reading file: $fileName" :: args._2, args._3, io.Source.stdin)
    }).getOrElse ("Please input commands (command<ENTER>). To end press CTRL+D." :: args._1, args._2, args._3, io.Source.stdin)

  val tableBoundsRegex = """(\d+):(\d+)""".r
  val DEFAULT_TABLE_BOUNDS = "5:5"
  /**
    * Coordinates are zero based so we need to subtract 1 from desired size.
    * @return
    */
  def configureBounds(args: (Messages, Errors, Reporter, BufferedSource)): (Messages, Errors, Reporter, BufferedSource, Coordinates) =
    getEnv(TABLE_BOUNDS, DEFAULT_TABLE_BOUNDS) match {
      case tableBoundsRegex(x, y) => {
        val (maxX, maxY) = (x.toInt - 1, y.toInt - 1)
        if (maxX >= 0 && maxY >= 0) {
          (s"Table size: ${maxX + 1} by ${maxY + 1}" :: args._1, args._2, args._3, args._4, Coordinates(maxX, maxY))
        } else {
          (args._1, s"Table size must be at least 1x1, but was: ${x}x${y})" :: args._2, args._3, args._4, Coordinates(maxX, maxY))
        }
      }
      case _ => {
        (args._1, s"Table size must be specified as Width:Height (eg: 4:4), but was: ${getEnv(TABLE_BOUNDS, "")})" :: args._2, args._3, args._4, Coordinates(4, 4))
      }
    }

  /**
    * This assures that a closeable resource R will be closed after processing.
    * @param resource R that can be closed
    * @param f funtion that does some processing of resource R
    * @tparam R
    * @tparam T
    * @return
    */
  private def withOpen[R <: { def close(): Unit }, T](resource: R)(f: R => T): T = try {
    f(resource)
  } finally {
    resource.close()
  }

  val ERROR_CODE = 666
  private def reportOrTerminateIfErrors(messages: Messages, errors: Errors) = {
    if(errors.nonEmpty) {
      errors.reverse.foreach(println)
      sys.exit(ERROR_CODE)
    }
    if(messages.nonEmpty) {
      messages.reverse.foreach(println)
    }
  }

  def getEnv(key: String, default: String): String = {
    scala.util.Properties.envOrElse(key, default)
  }

  def getEnv(key: String): Option[String] = {
    scala.util.Properties.envOrNone(key)
  }

  def usage(): Unit = {
    println(
      """Run toy robot. Commands can be specified passed via STDIN or as text file. Available commands are:
        |PLACE X,Y,F (where X, Y have to be within table bounds - or else will be ignored, F is direction: NORTH/SOUTH/EAST/WEST)
        |MOVE
        |RIGHT
        |LEFT
        |REPORT (println information about robot's position and bearing)
        |
        |You can customise the program by using environment properties:
        |REPORTER_CLASS - Reporter, one of: rea.robot.ConsoleReporter or rea.robot.NoopReporter
        |INPUT_FILE - input file to read commands from (otherwise commands will be read from STDIN)
        |TABLE_BOUNDS - set size of the table robot moves around, specified as: width:height, default 4:4
      """.stripMargin)
  }
}
