package com.sconysoft.robocode

import akka.cluster._
import akka.cluster.ClusterEvent._
import akka.actor._
import scala.util.Random

class Player extends Actor {
  
  val cluster = Cluster(context.system)
  val myPath = self.path
  val myFullPath = cluster.selfAddress + "/user/player"
  println(myFullPath)
  val random = new scala.util.Random

  val nMembers = context.system.settings.config.getInt("akka.cluster.min-nr-of-members")
  var members: Set[Member] = Set()

  var barrierIn = 0
  var barrierOut = 0

  var proposals: Set[Int] = Set()
  var round = 0
  var decided = false
  var decision: Int = _
  var correctThisRound: Set[String] = Set()
  var correctLastRound: Set[String] = Set()

  override def preStart(): Unit = cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def tryPass {
    if (barrierIn == nMembers && barrierOut == nMembers) {
      println(myPath + " reached barrier... agreeing now")
      context.become(agreeing)
      self ! Propose
    }
  }
  
  def waiting: Receive = {
    case MemberUp(member) => {
      println("Member is Up: " + member.address)
      members += member
      val ref = context.actorSelection(RootActorPath(member.address) + "user/player")
      ref ! "Hi there"
      ref ! Barrier
      barrierOut += 1
      tryPass
    }
    case UnreachableMember(member) => println("Member is Unreachable: " + member.address)
    case MemberRemoved(member, previousStatus) => println("Member Removed: " + member.address)

    case msg: String => println(msg + " ||| " + sender.path)
    case Barrier => {
      barrierIn += 1
      tryPass
    }

    case _ =>
  }

  def tryDecide {
    val correct = members.map(member => {
      val path = RootActorPath(member.address) + "user/player"
      if (path == myFullPath)
        myPath.toString
      else
        path
    })
    if (correct.subsetOf(correctThisRound) && !decided) {
      if (correctThisRound == correctLastRound) {
        decision = proposals.min
        decided = true
        println("decided: " + decision + " " + proposals)
        members.foreach(member => context.actorSelection(RootActorPath(member.address) + "user/player") ! Decided(decision,round))
      } else {
        round += 1
        correctLastRound = correctThisRound
        correctThisRound = Set()
        println("nondecided")
        members.foreach(member => context.actorSelection(RootActorPath(member.address) + "user/player") ! Proposed(proposals,round))
      }
    }
  }

  def agreeing: Receive = {
    case MemberUp(member) => {
      println("Member is Up: " + member.address)
      context.actorSelection(RootActorPath(member.address) + "user/player") ! "Too late buddy"
    }
    case UnreachableMember(member) => println("Member is Unreachable: " + member.address)
    case MemberRemoved(member, previousStatus) => {
      println("Member Removed: " + member.address)
      members -= member
    }

    case msg: String => println(msg + " ||| " + sender.path)
    case Propose => {
      correctLastRound = members.map(member => {
        val path = RootActorPath(member.address) + "user/player"
        if (path == myFullPath)
          myPath.toString
        else
          path
      })
      proposals += random.nextInt(61) + 40
      members.foreach(member => context.actorSelection(RootActorPath(member.address) + "user/player") ! Proposed(proposals,round))
    }
    case Proposed(values: Set[Int], round: Int) => {
      proposals ++= values
      correctThisRound += sender.path.toString
      tryDecide
    }
    case Decided(value: Int, round: Int) => {
      if (!decided) {
        decision = value
        decided = true
        println("decided: " + decision + " " + proposals)
        members.foreach(member => context.actorSelection(RootActorPath(member.address) + "user/player") ! Decided(decision,round+1))
      }
    }

    case _ =>
  }

  def receive = waiting

}
