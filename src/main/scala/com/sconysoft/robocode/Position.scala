package com.sconysoft.robocode

class Position (xInit: Int, yinit: Int) {
  val x: Int = xInit
  val y: Int = yinit

  def equals (other: Position): Boolean = {
    return this.x == other.x && this.y == other.y
  }
}
