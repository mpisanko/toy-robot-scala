package rea.robot

import scala.io.BufferedSource

object Main {

  import scala.util.{Either,Right,Left}

  val USAGE_STRING: String =
    """Run toy robot. Commands can be specified passed via STDIN or as text file. Available commands are:
      |PLACE X,Y,F (where X, Y have to be within table bounds - or else will be ignored,
      |                   F is direction: NORTH/SOUTH/EAST/WEST)
      |MOVE
      |RIGHT
      |LEFT
      |REPORT (println information about robot's position and bearing)
      |
      |You can customise the program by using environment variables:
      |REPORTER - Reporter, one of: console, string
      |INPUT_FILE - input file to read commands from (otherwise commands will be read from STDIN)
      |TABLE_BOUNDS - set size of the table robot moves around, specified as: width:height, default 4:4
    """.stripMargin

  def main(args: Array[String]): Unit = args.toList match {
    case ("--help" :: Nil) => printUsage()
    case _ => runRobot()
  }

  /**
    * Read Robot configuration and let Robot process commands from configured Source if no config errors found.
    * @return Robot in position after processing all commands
    */
  def runRobot(): Either[String, Robot] = Configuration.buildRobotsConfiguration(Configuration()) match {
      case Right(rc) =>
        rc.messages.foreach(println)
        val unplacedRobot = Robot(NotPlaced, rc.bounds, rc.reporter)
        val robot = withOpen(rc.source)(processInput(unplacedRobot))
        robot.reporter.report
        Right(robot)
      case Left(err) =>
        println(err)
        Left(err)
    }

  /**
    * Helper function for passing the commands to robot and closing Resource afterwards
    * @param robot Robot processing the commands
    * @param input commands from some source
    * @return Robot resulting from processing all the commands
    */
  private def processInput(robot: Robot)(input: BufferedSource): Robot = robot.processCommands(input.getLines)

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

  private def printUsage(): Unit = {
    println(USAGE_STRING)
  }
}
