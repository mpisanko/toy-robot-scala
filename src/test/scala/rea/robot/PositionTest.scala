package rea.robot

import org.scalatest.{FlatSpec, Matchers}
import Position.Coordinates

class PositionTest extends FlatSpec with Matchers {

  it should "not react to movement in case of NotPlaced" in {
    val np = NotPlaced
    np.left shouldEqual np
    np.right shouldEqual np
    np.move shouldEqual np
  }

  it should "react to movement in case of Placed" in {
    val n = Placed(Coordinates(0,0), North)
    n.left shouldEqual Placed(Coordinates(0,0), West)
    n.right shouldEqual Placed(Coordinates(0,0), East)
    n.move shouldEqual Placed(Coordinates(0,1), North)

    val s = Placed(Coordinates(0,0), South)
    s.left shouldEqual Placed(Coordinates(0,0), East)
    s.right shouldEqual Placed(Coordinates(0,0), West)
    s.move shouldEqual Placed(Coordinates(0,-1), South)
  }

  it should "place new position in all cases" in {
    val newPos = Placed(Coordinates(1,2), West)
    NotPlaced.place(newPos) shouldEqual newPos
    Placed(Coordinates(0, 0), East).place(newPos) shouldEqual newPos
  }
}
