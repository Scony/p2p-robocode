package com.sconysoft.robocode

import akka.cluster._
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration._
import scala.util.Random
import scala.language.postfixOps

class Player extends Actor with Stash{
  
  val cluster = Cluster(context.system)
  val myPath = self.path
  val myFullPath = cluster.selfAddress + "/user/player"
  val random = new scala.util.Random

  val nMembers = context.system.settings.config.getInt("akka.cluster.min-nr-of-members")
  var members: Set[Member] = Set()

  // waiting
  var barrierIn = 0
  var barrierOut = 0

  // agreeing
  var proposals: Set[Int] = Set()
  var round = 0
  var decided = false
  var decision: Int = _
  var proposedValue: Int = _
  var correctThisRound: Set[String] = Set()
  var correctLastRound: Set[String] = Set()

  // playing
  var robots: Set[ActorRef] = Set()

  println(myFullPath)

  override def preStart(): Unit = cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  override def postStop(): Unit = cluster.unsubscribe(self)

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _ => Resume
    }

  def startRobots {
    robots.foreach(robot => context.stop(robot))
    // robots = Set(context.actorOf(Props[Robot], name = "robot1"),context.actorOf(Props[Robot], name = "robot2"))

    if (decision.equals(proposedValue))
      robots = Set(context.actorOf(Props(new Robot(new OffensiveStrategy())), name = "robot1"))
    else
      robots = Set(context.actorOf(Props(new Robot(new DummyStrategy())), name = "robot1"))
  }

  //waiting

  def tryPass {
    if (barrierIn == nMembers && barrierOut == nMembers) {
      println("barrier reached... agreeing now")
      context.become(agreeing)
      unstashAll
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

    case _ => stash // postpone unknown messages
  }

  // agreeing

  def decide(value: Int) {
    decision = value
    decided = true
    println("decided: " + decision + " " + proposals)
    startRobots
    context.become(playing)
    unstashAll
    val stringified = members.map(member => member.address.toString + "/user/player")
    robots.foreach(robot => robot ! Start(stringified,decision))
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
        decide(proposals.min)
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
      proposedValue = random.nextInt(20) + 10
      proposals += proposedValue
      members.foreach(member => context.actorSelection(RootActorPath(member.address) + "user/player") ! Proposed(proposals,round))
    }
    case Proposed(values: Set[Int], round: Int) => {
      proposals ++= values
      correctThisRound += sender.path.toString
      tryDecide
    }
    case Decided(value: Int, round: Int) => {
      if (!decided) {
        decide(value)
        members.foreach(member => context.actorSelection(RootActorPath(member.address) + "user/player") ! Decided(decision,round+1))
      }
    }

    case _ => stash // postpone unknown messages
  }

  // playing

  def playing: Receive = {
    case MemberUp(member) => {
      println("Member is Up: " + member.address)
      context.actorSelection(RootActorPath(member.address) + "user/player") ! "Too late buddy"
    }
    case UnreachableMember(member) => println("Member is Unreachable: " + member.address)
    case MemberRemoved(member, previousStatus) => {
      println("Member Removed: " + member.address)
      members -= member
      println("restarting...")
      startRobots
    }

    case msg: String => println(msg + " ||| " + sender.path)
    case Request => sender ! Response(robots.map(robot => robot.path.name))

    case _ =>
  }

  def receive = waiting

}
