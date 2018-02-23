package rea.robot

import rea.robot.Position.Coordinates

import scala.collection.mutable

trait Reporter {
  def report(coordinates: Coordinates, direction: Direction): Unit
}

/**
  * Reporter writing to STDIN
  */
class ConsoleReporter extends Reporter {
  def report(coordinates: Coordinates, direction: Direction): Unit =
    println(s"Current position is: ${coordinates.x}, ${coordinates.y} ${direction.toString.toUpperCase}")
}

/**
  * Noop reporter
  */
class NoopReporter extends Reporter {
  def report(coordinates: Coordinates, direction: Direction): Unit = ()
}
/**
  * Reporter logging to file
  */
class StringListReporter extends Reporter {
  val reports = new mutable.ListBuffer[String]
  override def report(coordinates: Position.Coordinates, direction: Direction): Unit = {
    reports.append(s"${coordinates.x},${coordinates.y},$direction")
  }
}

