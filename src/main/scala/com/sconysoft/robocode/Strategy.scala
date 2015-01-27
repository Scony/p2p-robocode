package com.sconysoft.robocode

import com.sconysoft.robocode.GameState._

import scala.collection.mutable.ArrayBuffer

abstract class Strategy () {
  var map: GameMap = _
  var robots: Set[String] = _
  var selfName: String = _
  var state: GameState = GameState.INIT
  var validatedMapSize: Int = _
  var robotPosition: Position = new Position(0, 0)
  var forbiddenPositions: ArrayBuffer[Position] = new ArrayBuffer[Position]()

  def randPosition (forbiddenPositions: ArrayBuffer[Position]): Position

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

      }
      case "bomb" => {

      }
      case _ =>
    }
  }

  def move: String = {
    Thread.sleep(4000)

    try {
      if (state == GameState.INIT) {
        if (forbiddenPositions.size.equals(robots.size)) {
          state = GameState.PLAY

          // update & redraw map
          forbiddenPositions.foreach(e => map.setPlayer(e))
          map.refresh()
        } else {
          if (robotPosition.x == 0 && robotPosition.y == 0) {
            // rand new position
            robotPosition = randPosition(forbiddenPositions)
            return "set " + robotPosition.x + ' ' + robotPosition.y
          } else {
            // send our Position
            return "set " + robotPosition.x + ' ' + robotPosition.y
          }
        }

        forbiddenPositions.clear()
      }

      if (state == GameState.PLAY) {

      }
    } catch {
      case ex: Exception => println(ex)
    }

    return "void"
  }
}
