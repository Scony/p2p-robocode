package com.sconysoft.robocode

import akka.cluster._
import akka.actor._

class Robot extends Actor with Stash {

  val cluster = Cluster(context.system)
  val myPath = self.path
  val myFullPath = cluster.selfAddress + "/user/player/" + self.path.name

  // exploring
  var players: Set[String] = Set()
  var robots: Set[String] = Set()

  // playing
  var seqNo = 0
  var vector: Map[String,Int] = Map()
  var messages: Set[(String, Int, String)] = Set()

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
        vector ++= this.robots.map(robot => robot -> 0)
        println(vector)
        context.become(playing)
        unstashAll()
        self ! Move
      }
    }

    case _ => stash // postpone unknown messages
  }

  // playing

  def playing: Receive = {
    case Move => {
      seqNo += 1
      this.robots.foreach(robot => context.actorSelection(robot) ! Bcast("msg1/" + myFullPath,seqNo))
      seqNo += 1
      this.robots.foreach(robot => context.actorSelection(robot) ! Bcast("msg2/" + myFullPath,seqNo))
      seqNo += 1
      this.robots.foreach(robot => context.actorSelection(robot) ! Bcast("msg3/" + myFullPath,seqNo))
    }
    case Bcast(message: String, seq: Int) => {
      messages += ((message,seq,sender.path.toString))
      vector = vector.updated(sender.path.toString,seq)
      println(vector)
      val toDeliver = messages.filter(message => message._2 <= vector.values.min)
      messages --= toDeliver
      val deliverable = toDeliver.map(m => (m._1,m._2,m._3.replace(self.path.root.toString,cluster.selfAddress.toString+"/"))).toList.sortBy(_._3)
      deliverable.foreach(msg => self ! ABcast(msg._1,msg._3.replace(cluster.selfAddress.toString+"/",self.path.root.toString)))
    }
    case ABcast(message: String, from: String) => println("ABcast: " + message + "========" + from)

    case _ =>
  }

  def receive = exploring

}
