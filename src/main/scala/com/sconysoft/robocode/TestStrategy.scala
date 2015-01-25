package com.sconysoft.robocode

class TestStrategy () extends Strategy {

  var seq = 0
  var map: GameMap = _

  override def init(robots: Set[String], self: String, mapSize: Int) {
    println("init")
    println("robots: " + robots)
    println("self: " + self)
    println("mapsize: " + mapSize)

    map = new GameMap (mapSize)
    map.initializeGraphics()
  }

  override def update(order: String, from: String) {
    println("update: " + order + ' ' + from)
  }

  override def move: String = {
    Thread.sleep(4000)
    seq += 1
    if (seq == 3)
      "EOF"
    else
      "msg" + seq
  }

}
