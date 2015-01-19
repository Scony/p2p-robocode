package com.sconysoft.robocode

import akka.cluster._
import akka.actor._

class Robot(strategy: Strategy) extends Actor with Stash {

  val cluster = Cluster(context.system)
  val myPath = self.path.toString
  val myFullPath = cluster.selfAddress + "/user/player/" + self.path.name

  // exploring
  var players: Set[String] = Set()
  var robots: Set[String] = Set()
  var mapSize = 0

  // playing
  var seqNo = 0
  var vector: Map[String,Int] = Map()
  var messages: Set[(String, Int, String)] = Set()
  var ABcounter = 0

  Thread.sleep(3000)
  println(myFullPath)

  // exploring

  def exploring: Receive = {
    case Start(players: Set[String], mapSize: Int) => {
      this.players = players
      this.mapSize = mapSize
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
        strategy.init(this.robots,myPath,mapSize)
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
      val order = strategy.move
      if (order != "EOF") {
        seqNo += 1
        this.robots.foreach(robot => context.actorSelection(robot) ! Bcast(order,seqNo))
      } else {
        // TODO get winner and pass somewhere (prolly to player)
      }
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
    case ABcast(message: String, from: String) => {
      println("ABcast: " + message + "========" + from)
      strategy.update(message,from)
      ABcounter += 1
      if (ABcounter == robots.size) {
        ABcounter = 0
        self ! Move
      }
    }

    case _ =>
  }

  def receive = exploring

}
