package rea.robot

sealed trait Command
final case object Move extends Command
final case object Left extends Command
final case object Report extends Command
final case class Place(x: Int, y: Int, direction: Direction) extends Command
