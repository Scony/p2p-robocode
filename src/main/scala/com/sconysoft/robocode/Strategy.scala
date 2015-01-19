package com.sconysoft.robocode

abstract class Strategy {
  def init(robots: Set[String], self: String, mapSize: Int)
  def update(order: String, from: String)
  def move: String
}
