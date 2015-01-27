package com.sconysoft.robocode

import com.sconysoft.robocode.GameState._

import scala.collection.mutable.ArrayBuffer

abstract class Strategy () {
  val sleepTime: Int = 2000
  var map: GameMap = _
  var robots: Set[String] = _
  var selfName: String = _
  var state: GameState = GameState.INIT
  var validatedMapSize: Int = _
  var robotPosition: Position = new Position(0, 0)
  var forbiddenPositions: ArrayBuffer[Position] = new ArrayBuffer[Position]()
  var bombs: List[Int] = List[Int]()

  def randPosition (forbiddenPositions: ArrayBuffer[Position]): Position
  def generateMove (): String

  def init(robots: Set[String], self: String, mapSize: Int) {
    println("init")
    println("robots: " + robots)
    println("self: " + self)
    println("mapsize: " + mapSize)

    this.robots = robots
    this.selfName = self

    map = new GameMap (mapSize)
    validatedMapSize = map.getMapSize()

    map.initializeGraphics()
  }

  def update(order: String, from: String) {
    try {
      val move = order.split(" +")

      move(0) match {
        case "set" => {
          if (from != selfName) {
            val otherPosition = new Position (move(1).toInt, move(2).toInt)
            if (otherPosition == robotPosition && from < selfName) {
              robotPosition = new Position(0, 0)
            }
            if (!forbiddenPositions.contains(new Position(move(1).toInt, move(2).toInt)))
              forbiddenPositions.append(new Position(move(1).toInt, move(2).toInt))
          } else {
            if (!forbiddenPositions.contains(new Position(move(1).toInt, move(2).toInt)))
              forbiddenPositions.append(new Position(move(1).toInt, move(2).toInt))
          }
        }
        case "move" => {
          if (from != selfName) {
            if (map.isValid(new Position(move(3).toInt, move(4).toInt)))
              map.move(new Position(move(1).toInt, move(2).toInt), new Position(move(3).toInt, move(4).toInt))
          } else {
            if (map.isValid(new Position(move(3).toInt, move(4).toInt))) {
              map.move(robotPosition, new Position(move(3).toInt, move(4).toInt))
              robotPosition = new Position(move(3).toInt, move(4).toInt)
            }
          }
        }
        case "bomb" => {
          if (from != selfName) {
            if (map.isValid(new Position(move(1).toInt, move(2).toInt)))
              map.setBomb(new Position(move(1).toInt, move(2).toInt))
          } else {
            if (map.isValid(new Position(move(1).toInt, move(2).toInt))) {
              map.setBomb(new Position(move(1).toInt, move(2).toInt))
              bombs = bombs ::: 3 :: List()
              bombs = bombs.filter(e => e > 0)
            }
          }
        }
        case _ =>
      }
    } catch {
      case ex: Exception => println(ex)
    }
  }

  def move: String = {
    Thread.sleep(sleepTime)

    try {
      if (state == GameState.INIT) {
        if (forbiddenPositions.size.equals(robots.size)) {
          state = GameState.PLAY

          // update & redraw map
          forbiddenPositions.foreach(e => map.setPlayer(e))
          map.refresh()
          Thread.sleep(sleepTime)
        } else {
          if (robotPosition.x == 0 && robotPosition.y == 0) {
            // rand new position
            robotPosition = randPosition(forbiddenPositions)
            return "set " + robotPosition.x + ' ' + robotPosition.y
          } else {
            // send our position
            return "set " + robotPosition.x + ' ' + robotPosition.y
          }
        }

        forbiddenPositions.clear()
      }

      if (state != GameState.INIT && isEnd()) {
        state = GameState.END
        map.refresh()
        return "eof"
      }

      if (state == GameState.PLAY) {
        map.tick()
        bombs = bombs.map(e => e -1).filter(e => e > 0)

        if (!map.isAlive(robotPosition)) {
          if (isEnd()) {
            state = GameState.END
            map.refresh()
            return "eof"
          } else {
            state = GameState.DEAD
            map.refresh()
            return "nop"
          }
        }

        map.refresh()
        return generateMove
      }

      map.refresh()
    } catch {
      case ex: Exception => println(ex)
    }

    return "nop"
  }

  def isEnd (): Boolean = {
    return map.countPlayers().equals(1)
  }
}
