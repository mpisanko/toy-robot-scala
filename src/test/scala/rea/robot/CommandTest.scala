package rea.robot

import org.scalatest.{FlatSpec, Matchers}

class CommandTest extends FlatSpec with Matchers {

  it should "parse commands from string" in {
    Command.parse("riGht") shouldEqual Some(Right)
    Command.parse(" rEporT  ") shouldEqual Some(Report)
    Command.parse("LEFT") shouldEqual Some(Left)
    Command.parse("move") shouldEqual Some(Move)
    Command.parse("Place 2, 4 , North") shouldEqual Some(Place(Placed(2, 4, North)))
    Command.parse("  plACe   0, 3,    easT   ") shouldEqual Some(Place(Placed(0, 3, East)))
  }

  it should "ignore incorrect commands" in {
    Command.parse("North ") shouldEqual None
    Command.parse("PLACE") shouldEqual None
    Command.parse("PLAC , 2, -8, direction") shouldEqual None
    Command.parse("") shouldEqual None
    Command.parse("go home") shouldEqual None
  }
}
