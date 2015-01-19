package com.sconysoft.robocode

class TestStrategy extends Strategy {

  var seq = 0

  override def init(robots: Set[String], self: String, mapSize: Int) {
    println("init")
    println("robots: " + robots)
    println("self: " + self)
    println("mapsize: " + mapSize)
  }
  override def update(order: String, from: String) {
    println("update: " + order)
  }
  override def move: String = {
    seq += 1
    if (seq == 3)
      "EOF"
    else
      "msg" + seq
  }

}
