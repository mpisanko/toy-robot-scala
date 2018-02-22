package rea.robot

import org.scalatest.{FlatSpec, Matchers}

class DirectionTest extends FlatSpec with Matchers {

  it should "parse direction from string regardless of case and whitespace" in {
    Direction.parse("north") shouldEqual Some(North)
    Direction.parse("South") shouldEqual Some(South)
    Direction.parse("     eAsT") shouldEqual Some(East)
    Direction.parse(" WESt    ") shouldEqual Some(West)
  }

  it should "ignore bogus entries" in {
    Direction.parse("") shouldEqual None
    Direction.parse("   ") shouldEqual None
    Direction.parse("not a direction") shouldEqual None
    Direction.parse("southeast") shouldEqual None
  }

  it should "get back to self after four rotations the same way" in {
    Seq(North, East, South, West).foreach(d => d.left.left.left.left shouldEqual d)
    Seq(North, East, South, West).foreach(d => d.right.right.right.right shouldEqual d)
  }
}
