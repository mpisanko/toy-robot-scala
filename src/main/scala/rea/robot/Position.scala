package rea.robot

sealed trait Position
case object NotPlaced extends Position
case class Placed(x: Int, y: Int, direction: Direction) extends Position

