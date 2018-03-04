package rea.robot

import scala.collection.mutable

trait Reporter {
  val reports = new mutable.ListBuffer[String]
  def addReport(report: String): Reporter = {
    reports.append(report)
    this
  }
  type T
  def report(): T
}

/**
  * Reporter writing to STDIN
  */
class ConsoleReporter extends Reporter {
  override type T = Unit
  def report(): T = {
    reports.foreach(println)
  }
}

/**
  * Noop reporter
  */
class NoopReporter extends Reporter {
  override type T = Unit
  def report(): T = ()
}
/**
  * Reporter logging to file
  */
class StringListReporter extends Reporter {
  override type T = List[String]
  override def report(): T = {
    reports.toList
  }
}
