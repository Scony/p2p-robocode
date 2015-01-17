package com.sconysoft.robocode

case object Barrier
case object Propose
case object Start
case class Proposed(values: Set[Int], round: Int)
case class Decided(value: Int, round: Int)
