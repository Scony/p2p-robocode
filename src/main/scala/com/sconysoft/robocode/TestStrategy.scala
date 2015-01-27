package com.sconysoft.robocode

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class TestStrategy () extends Strategy {
  def randPosition (forbiddenPositions: ArrayBuffer[Position]): Position = {
    val positions: ArrayBuffer[Position] = new ArrayBuffer[Position]()

    for (x: Int <- 1 to validatedMapSize - 2)
      for (y: Int <- 1 to validatedMapSize - 2)
        if ((x % 2 != 0 || y % 2 != 0) && !forbiddenPositions.contains(new Position(x, y)))
          positions.append(new Position(x, y))

    val num = positions.size
    var r = new Random()

    return positions(r.nextInt(num))
  }


}
