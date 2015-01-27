package com.sconysoft.robocode

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class DummyStrategy () extends Strategy {
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

  override def generateMove (): String = {
    val r = Random
    val x = robotPosition.x
    val y = robotPosition.y

    var possibleMoves: List[Position] = List[Position]()

    if(map.isValid(new Position(x, y - 1)))
      possibleMoves = new Position(x, y - 1) :: possibleMoves

    if(map.isValid(new Position(x, y + 1)))
      possibleMoves = new Position(x, y + 1) :: possibleMoves

    if(map.isValid(new Position(x - 1, y)))
      possibleMoves = new Position(x - 1, y) :: possibleMoves

    if(map.isValid(new Position(x + 1, y)))
      possibleMoves = new Position(x + 1, y) :: possibleMoves

    if (possibleMoves.size > 0) {
      val position: Position = possibleMoves(r.nextInt(possibleMoves.size))

      if (r.nextInt(100) > 80 && bombs.size < 3) {
        // set a bomb
        return "bomb " + position.x + ' ' + position.y
      } else {
        // make a move
        return "move " + robotPosition.x + ' ' + robotPosition.y + ' ' + position.x + ' ' + position.y
      }
    } else {
      return "nop"
    }
  }
}
