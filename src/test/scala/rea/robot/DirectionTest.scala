package rea.robot

import org.scalatest.{FlatSpec, Matchers}

class DirectionTest extends FlatSpec with Matchers {

  it should "get back to self after four rotations the same way" in {
    Seq(North, East, South, West).foreach(d => d.left.left.left.left shouldEqual d)
    Seq(North, East, South, West).foreach(d => d.right.right.right.right shouldEqual d)
  }
}
