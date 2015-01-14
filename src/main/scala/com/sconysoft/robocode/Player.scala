package com.sconysoft.robocode

import akka.cluster._
import akka.cluster.ClusterEvent._
import akka.actor._

class Player extends Actor {
  
  val cluster = Cluster(context.system)
  val me = self.path
  val officialMe = cluster.selfAddress + "/user/player/"

  override def preStart(): Unit = cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  override def postStop(): Unit = cluster.unsubscribe(self)
  
  def receive = {
    case MemberUp(member) => {
      println("Member is Up: " + member.address)
      context.actorSelection(RootActorPath(member.address) + "/user/player") ! "Hi there, it's " + me
    }
    case UnreachableMember(member) => println("Member is Unreachable: " + member.address)
    case MemberRemoved(member, previousStatus) => println("Member Removed: " + member.address)
    case msg: String => println(msg + " ||| " + sender.path)
    case _: MemberEvent =>
  }

}
