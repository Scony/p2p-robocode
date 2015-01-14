package com.sconysoft.robocode

import akka.actor._
import scala.swing._
import scala.swing.event._
import java.awt.Color
import java.awt.Graphics2D

object Test extends SimpleSwingApplication {
  
  sealed trait MyMessage
  case object Ping extends MyMessage
  case object Go extends MyMessage
  case class Broadcast(who: String) extends MyMessage

  class Canvas(label: Label) extends Panel {
    var x = 0

    override def paintComponent(g: Graphics2D) {
      x += 1
      label.text = "" + x
      g.clearRect(0, 0, size.width, size.height)
      g.setColor(Color.orange)
      g.fillRect(0, 0, size.width, size.height)
      g.setColor(Color.blue)
      g.fillOval(0, 0, size.width, size.height)
    }
  }

  def top = new MainFrame {
    title = "p2p robocode"

    val button = new Button {
      text = "Akka test"
    }
    val button2 = new Button {
      text = "Redraw"
    }
    val label = new Label {
      text = "No button clicks registered"
    }
    val canvas = new Canvas(label) {
      preferredSize = new Dimension(500,500)
    }

    contents = new BorderPanel {
      layout(button) = BorderPanel.Position.West
      layout(button2) = BorderPanel.Position.North
      layout(label) = BorderPanel.Position.South
      layout(canvas) = BorderPanel.Position.Center
    }

    listenTo(button, button2)
    reactions += {
      case ButtonClicked(c) if c == button =>
        runAkka
      case ButtonClicked(c) if c == button2 =>
        canvas.repaint()
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
    Thread.sleep(1000);
    val system = ActorSystem("MySystem")
    val drawer = system.actorOf(Props(new Drawer), name = "drawer")
    drawer ! Ping
    system.actorSelection("/user/drawer/clientA/robotA") ! Go
    system.actorFor("/user/drawer/clientA/robotA") ! Go
  }

}
