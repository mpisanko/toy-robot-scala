package rea.robot

import org.scalatest.{FlatSpec, Matchers}

class DirectionTest extends FlatSpec with Matchers {
  it should "parse direction from string regardless of case and whitespace" in {
    Direction("north") shouldEqual Some(North)
    Direction("South") shouldEqual Some(South)
    Direction(" eAsT") shouldEqual Some(East)
    Direction(" WESt    ") shouldEqual Some(West)
  }
  it should "ignore bogus entries" in {
    Direction("") shouldEqual None
    Direction("   ") shouldEqual None
    Direction("not a direction") shouldEqual None
    Direction("southeast") shouldEqual None
  }
}
