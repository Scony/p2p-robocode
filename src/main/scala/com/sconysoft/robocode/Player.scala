package com.sconysoft.robocode

import akka.cluster._
import akka.cluster.ClusterEvent._
import akka.actor._

class Player extends Actor {
  
  val cluster = Cluster(context.system)
  val me = self.path
  // val officialMe = cluster.selfAddress + "/user/player/"

  val nMembers = context.system.settings.config.getInt("akka.cluster.min-nr-of-members")
  var members: Set[Member] = Set()
  var barrierIn = 0
  var barrierOut = 0

  override def preStart(): Unit = cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def tryPass {
    if (barrierIn == nMembers && barrierOut == nMembers) {
      println(me + " reached barrier")
      context.become(playing)
    }
  }
  
  def waiting: Receive = {
    case MemberUp(member) => {
      println("Member is Up: " + member.address)
      members += member
      val ref = context.actorSelection(RootActorPath(member.address) + "/user/player")
      ref ! "Hi there"
      ref ! Barrier
      barrierOut += 1
      tryPass
    }
    case UnreachableMember(member) => println("Member is Unreachable: " + member.address)
    case MemberRemoved(member, previousStatus) => println("Member Removed: " + member.address)
    case _: MemberEvent =>

    case msg: String => println(msg + " ||| " + sender.path)
    case Barrier => {
      barrierIn += 1
      tryPass
    }
  }

  def playing: Receive = {
    case MemberUp(member) => {
      println("Member is Up: " + member.address)
      context.actorSelection(RootActorPath(member.address) + "/user/player") ! "Too late buddy"
    }
    case UnreachableMember(member) => println("Member is Unreachable: " + member.address)
    case MemberRemoved(member, previousStatus) => println("Member Removed: " + member.address)
    case _: MemberEvent =>

    case msg: String => println(msg + " ||| " + sender.path)

    case _ =>
  }

  def receive = waiting

}
