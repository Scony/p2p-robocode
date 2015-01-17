package com.sconysoft.robocode

import akka.cluster._
import akka.actor._

class Robot extends Actor {

  val cluster = Cluster(context.system)
  val myPath = self.path
  val myFullPath = cluster.selfAddress + "/user/player/" + self.path.name

  // exploring
  var players: Set[String] = Set()
  var robots: Set[String] = Set()

  // playing
  // TODO

  Thread.sleep(3000)
  println(myFullPath)

  // exploring

  def exploring: Receive = {
    case Start(players: Set[String]) => {
      this.players = players
      players.foreach(node => context.actorSelection(node) ! Request)
    }
    case Response(robots: Set[String]) => {
      val senderPath = if (sender.path == self.path.parent) cluster.selfAddress + "/user/player" else sender.path.toString
      players -= senderPath
      this.robots ++= robots.map(robot => sender.path + "/" + robot)
      if (players.isEmpty) {
        println(myPath + " playing")
        println(this.robots)
        context.become(playing)
      }
    }

    case unknown => self ! unknown // postpone unknown messages
  }

  // playing

  def playing: Receive = {
    case _ =>
  }

  def receive = exploring

}
