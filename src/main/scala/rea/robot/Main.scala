package rea.robot

import rea.robot.Configuration.{Errors, Messages}

import scala.io.BufferedSource

object Main {

  def main(args: Array[String]): Unit = {
    args.toList match {
      case ("--help" :: Nil) => usage()
      case _ => doProcessing()
    }
  }

  def doProcessing(): Robot = {
    lazy val (messages, errors, reporter, input, tableBounds) = Configuration.configureRobot((List(), List()))

    reportAndTerminateIfErrorsFound(messages, errors)

    val robot = Robot(NotPlaced, tableBounds, reporter)
    withOpen(input)(processInput(robot))
  }

  def processInput(robot: Robot)(input: BufferedSource): Robot =
    input.getLines.map(Command.parse).flatten.foldLeft(robot)(reducer(_, _))

  def reducer(robot: Robot, command: Command): Robot = robot.execute(command)


  /**
    * This assures that a closeable resource R will be closed after processing.
    * @param resource R that can be closed
    * @param f funtion that does some processing of resource R
    * @tparam R type param of Resource
    * @tparam T return type param
    * @return result of invoking function f with resource
    */
  private def withOpen[R <: { def close(): Unit }, T](resource: R)(f: R => T): T = try {
    f(resource)
  } finally {
    resource.close()
  }

  val ERROR_CODE = 666
  private def reportAndTerminateIfErrorsFound(messages: Messages, errors: Errors): Unit = {
    if(errors.nonEmpty) {
      errors.reverse.foreach(println)
      sys.exit(ERROR_CODE)
    }
    if(messages.nonEmpty) {
      messages.reverse.foreach(println)
    }
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
