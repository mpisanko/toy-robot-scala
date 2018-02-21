package rea.robot

import org.scalatest.{FlatSpec, Matchers}

class CommandTest extends FlatSpec with Matchers {

  it should "parse commands from string" in {
    Command("riGht") shouldEqual Some(Right)
    Command(" rEporT  ") shouldEqual Some(Report)
    Command("LEFT") shouldEqual Some(Left)
    Command("move") shouldEqual Some(Move)
    Command("Place 2, 4 , North") shouldEqual Some(Place(Placed(2, 4, North)))
    Command("  plACe   0, 3,    easT   ") shouldEqual Some(Place(Placed(0, 3, East)))
  }

  it should "ignore incorrect commands" in {
    Command("North ") shouldEqual None
    Command("PLACE") shouldEqual None
    Command("PLAC , 2, -8, direction") shouldEqual None
    Command("") shouldEqual None
    Command("go home") shouldEqual None
  }
}
