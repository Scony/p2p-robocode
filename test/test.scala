package com.sconysoft.test.multilevel

import akka.actor._

object Mlvl extends App {
  
  sealed trait MyMessage
  case object Ping extends MyMessage
  case object Go extends MyMessage
  case class Broadcast(who: String) extends MyMessage
  
  class Robot extends Actor {
    
    def receive = {
      case Ping => println("Robot here: " + self.path.name)
      case Broadcast(who) => println("Broadcast(" + who + ") here: " + self.path.parent + "/" +  self.path.name)
      case Go => {
        println("Go here: " + self.path.parent + "/" +  self.path.name)
        context.actorSelection("../../*/*") ! Broadcast("nvm")
      }
    }

  }
  
  class Client extends Actor {
    
    val robots = List(context.actorOf(Props(new Robot), name = "robotA"),context.actorOf(Props(new Robot), name = "robotB"))
    
    def receive = {
      case Ping => {
        println("client here: " + self.path.name)
        for (robot <- robots)
          robot ! Ping
      }
      case Broadcast(who) => println("Broadcast(" + who + ") here: " + self.path.parent + "/" +  self.path.name)
    }
    
  }
  
  class Drawer extends Actor {

    val clients = List(context.actorOf(Props(new Client), name = "clientA"),context.actorOf(Props(new Client), name = "clientB"))

    def receive = {
      case Ping => {
        println("drawer here: " + self.path.name)
        for (client <- clients)
          client ! Ping
      }
      case Broadcast(who) => println("Broadcast(" + who + ") here: " + self.path.parent + "/" + self.path.name)
    }
  }
  
  
  override def main(args: Array[String]) {

    val system = ActorSystem("MySystem")
    val drawer = system.actorOf(Props(new Drawer), name = "drawer")
    drawer ! Ping
    system.actorSelection("/user/drawer/clientA/robotA") ! Go
    system.actorFor("/user/drawer/clientA/robotA") ! Go
    
  }
}
