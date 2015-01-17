package com.sconysoft.robocode

import akka.cluster._
import akka.actor._

class Robot extends Actor {

  val cluster = Cluster(context.system)
  val myPath = self.path
  val myFullPath = cluster.selfAddress + "/user/player/" + self.path.name

  var x: Int = _

  println("hold on")
  Thread.sleep(3000)
  println(myPath)
  println(myFullPath)

  def receive = {
    case Start => {
      println("starting: " + x)
      x += 1
      x = 1/0
      println("done: " + x)
    }

    case _ =>
  }

}
