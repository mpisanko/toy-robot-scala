package rea.robot

import rea.robot.Position.Coordinates

trait Reporter {
  def report(coordinates: Coordinates, direction: Direction): Unit
}

class ConsoleReporter extends Reporter {
  def report(coordinates: Coordinates, direction: Direction): Unit =
    println(s"Current position is: ${coordinates.x}, ${coordinates.y} ${direction.toString.toUpperCase}")
}

/**
  * Noop class just to provide alternative implementation
  */
class NoopReporter extends Reporter {
  def report(coordinates: Coordinates, direction: Direction): Unit = ()
}