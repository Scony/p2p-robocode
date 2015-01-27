package com.sconysoft.robocode

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

class OffensiveStrategy () extends Strategy {
  var lastMove: Position = new Position(0, 0)

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

    if(map.isValid(new Position(x, y - 1)) && !map.isInDanger(new Position(x, y - 1)) && !(new Position(x, y - 1)).equals(lastMove))
      possibleMoves = (new Position(x, y - 1)) :: possibleMoves

    if(map.isValid(new Position(x, y + 1)) && !map.isInDanger(new Position(x, y + 1)) && !(new Position(x, y + 1)).equals(lastMove))
      possibleMoves = (new Position(x, y + 1)) :: possibleMoves

    if(map.isValid(new Position(x - 1, y)) && !map.isInDanger(new Position(x - 1, y)) && !(new Position(x - 1, y)).equals(lastMove))
      possibleMoves = (new Position(x - 1, y)) :: possibleMoves

    if(map.isValid(new Position(x + 1, y)) && !map.isInDanger(new Position(x + 1, y)) && !(new Position(x + 1, y)).equals(lastMove))
      possibleMoves = (new Position(x + 1, y)) :: possibleMoves

    if (possibleMoves.size > 0) {
      var position: Position = new Position(0, 0)

      if (map.isInDanger(robotPosition)) {
        position = possibleMoves(r.nextInt(possibleMoves.size))
        return "move " + robotPosition.x + ' ' + robotPosition.y + ' ' + position.x + ' ' + position.y
      }

      val others: ArrayBuffer[Position] = map.getOthersPosition(robotPosition)
      val other = others(0)

      if (countDistance(robotPosition, other).equals(2)) {
        possibleMoves.foreach(e => {
          if (countDistance(e, other).equals(1))
            position = e
        })

        if (position.x.equals(0)) {
          position = possibleMoves(r.nextInt(possibleMoves.size))
          return "move " + robotPosition.x + ' ' + robotPosition.y + ' ' + position.x + ' ' + position.y
        }

        return "bomb " + position.x + ' ' + position.y
      } else {
        var min: Int = 3 * validatedMapSize
        possibleMoves.foreach(e => {
          if (countDistance(e, other) < min) {
            position = e
            min = countDistance(e, other)
          }
        })

        lastMove = position
        return "move " + robotPosition.x + ' ' + robotPosition.y + ' ' + position.x + ' ' + position.y
      }
    } else {
      return "nop"
    }
  }

  def countDistance (player: Position, other: Position): Int = {
    return math.abs(player.x - other.x) + math.abs(player.y - other.y)
  }
}