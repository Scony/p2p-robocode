package com.sconysoft.test.multilevel

import akka.actor._
import scala.swing._
import scala.swing.event._

object Mlvl extends SimpleSwingApplication {
  
  sealed trait MyMessage
  case object Ping extends MyMessage
  case object Go extends MyMessage
  case class Broadcast(who: String) extends MyMessage

  def top = new MainFrame {
    // http://vimeo.com/13900342
    title = "Reactive Swing App"
    val button = new Button {
      text = "Click me"
    }
    val button2 = new Button {
      text = "Akka test"
    }
    val label = new Label {
      text = "No button clicks registered"
    }
    contents = new BoxPanel(Orientation.Vertical) {
      contents += button
      contents += button2
      contents += label
      border = Swing.EmptyBorder(50, 50, 10, 30)
    }
    listenTo(button, button2)
    var nClicks = 0
    reactions += {
      case ButtonClicked(component) if component == button =>
        nClicks += 1
        label.text = "Number of button clicks: "+ nClicks
      case ButtonClicked(component) if component == button2 =>
        runAkka
    }
  }
  
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

  def runAkka = {
    val system = ActorSystem("MySystem")
    val drawer = system.actorOf(Props(new Drawer), name = "drawer")
    drawer ! Ping
    system.actorSelection("/user/drawer/clientA/robotA") ! Go
    system.actorFor("/user/drawer/clientA/robotA") ! Go
  }

}
