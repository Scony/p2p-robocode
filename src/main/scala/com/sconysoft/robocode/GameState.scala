package com.sconysoft.robocode

object GameState extends Enumeration {
  type GameState = Value
  val INIT, PLAY, DEAD, END = Value
}
