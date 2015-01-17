package com.sconysoft.robocode

// players
case object Barrier
case object Propose
case class Proposed(values: Set[Int], round: Int)
case class Decided(value: Int, round: Int)
case object Request

// robots
case class Start(players: Set[String])
case class Response(robots: Set[String])
