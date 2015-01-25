package com.sconysoft.robocode

import akka.cluster._
import akka.actor._
import com.typesafe.config.ConfigFactory
import language.postfixOps

object Main {

  def main(args: Array[String]) {
    val port = if (args.isEmpty) "0" else args(0)
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port").withFallback(ConfigFactory.load())

    val system = ActorSystem("ClusterSystem", config)
    system.actorOf(Props[Player], name = "player")
  }
}
