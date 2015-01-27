package com.sconysoft.robocode

import java.awt.image.BufferedImage
import java.awt.{Dimension, Graphics, GridBagConstraints}
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel

import com.sconysoft.robocode.MapObjectType.MapObjectType

trait MapObject {
  val destroyable: Boolean
  val movable: Boolean
  var position: Position = _
  val fieldType: MapObjectType
  val fileName: String

  val width: Int = 20
  val height: Int = 20

  def tick (): Boolean = {
    return false
  }

  def draw (panel: JPanel): Unit = {
    try {
      val imagePanel: ImagePanel = new ImagePanel(fileName)
      val gbc: GridBagConstraints = new GridBagConstraints()

      gbc.fill = GridBagConstraints.HORIZONTAL
      gbc.gridx = position.x
      gbc.gridy = position.y

      panel.add(imagePanel, gbc)
    } catch {
      case ex:Exception => println(ex)
    }
  }

  def init (position: Position) {
    this.position = position
  }
}

class ImagePanel (path: String) extends JPanel {
  val image: BufferedImage = ImageIO.read(new File(path))

  override protected def paintComponent (g: Graphics): Unit = {
    super.paintComponent(g)
    setPreferredSize(new Dimension(20, 20))
    setSize(new Dimension(20, 20))
    g.drawImage(image, 0, 0, null)
  }
}

object MapObject {
  private class Wall extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Wall
    override val fileName: String = "img/wall.png"
  }

  private class Brick extends MapObject {
    override val destroyable: Boolean = true
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Brick
    override val fileName: String = "img/bricks.png"
  }

  private class Ghost extends MapObject {
    override val destroyable: Boolean = true
    override val movable: Boolean = true
    override val fieldType: MapObjectType = MapObjectType.Ghost
    override val fileName: String = "img/ghost.png"
  }

  private class Explosion_up extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Explosion_up
    override val fileName: String = "img/explosion_up.png"
  }

  private class Explosion_down extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Explosion_down
    override val fileName: String = "img/explosion_down.png"
  }
  private class Explosion_left extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Explosion_left
    override val fileName: String = "img/explosion_left.png"
  }
  private class Explosion_right extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Explosion_right
    override val fileName: String = "img/explosion_right.png"
  }
  private class Explosion_middle extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Explosion_middle
    override val fileName: String = "img/explosion_center.png"
  }

  private class Bomb extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Bomb
    override val fileName: String = "img/bomb.png"
    var count: Int = 3

    override def tick (): Boolean = {
      count = count - 1

      return count.equals(0)
    }
  }

  private class Grass extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Grass
    override val fileName: String = "img/grass.png"
  }

  def apply (objectType: MapObjectType, position: Position): MapObject = {
    objectType match {
      case MapObjectType.Wall => {
        val wall = new Wall
        wall.init(position)
        return wall
      }
      case MapObjectType.Brick => {
        val brick = new Brick
        brick.init(position)
        return brick
      }
      case MapObjectType.Ghost => {
        val ghost = new Ghost
        ghost.init(position)
        return ghost
      }
      case MapObjectType.Bomb => {
        val bomb = new Bomb
        bomb.init(position)
        return bomb
      }
      case MapObjectType.Grass => {
        val grass = new Grass
        grass.init(position)
        return grass
      }
      case MapObjectType.Explosion_down => {
        val explosion = new Explosion_down
        explosion.init(position)
        return explosion
      }
      case MapObjectType.Explosion_up => {
        val explosion = new Explosion_up
        explosion.init(position)
        return explosion
      }
      case MapObjectType.Explosion_left => {
        val explosion = new Explosion_left
        explosion.init(position)
        return explosion
      }
      case MapObjectType.Explosion_right => {
        val explosion = new Explosion_right
        explosion.init(position)
        return explosion
      }
      case MapObjectType.Explosion_middle => {
        val explosion = new Explosion_middle
        explosion.init(position)
        return explosion
      }
    }
  }

  def getObject (objectType: MapObjectType, position: Position): MapObject = {
    objectType match {
      case MapObjectType.Wall => {
        val wall = new Wall
        wall.init(position)
        return wall
      }
      case MapObjectType.Brick => {
        val brick = new Brick
        brick.init(position)
        return brick
      }
      case MapObjectType.Ghost => {
        val ghost = new Ghost
        ghost.init(position)
        return ghost
      }
      case MapObjectType.Bomb => {
        val bomb = new Bomb
        bomb.init(position)
        return bomb
      }
      case MapObjectType.Grass => {
        val grass = new Grass
        grass.init(position)
        return grass
      }
      case MapObjectType.Explosion_down => {
        val explosion = new Explosion_down
        explosion.init(position)
        return explosion
      }
      case MapObjectType.Explosion_up => {
        val explosion = new Explosion_up
        explosion.init(position)
        return explosion
      }
      case MapObjectType.Explosion_left => {
        val explosion = new Explosion_left
        explosion.init(position)
        return explosion
      }
      case MapObjectType.Explosion_right => {
        val explosion = new Explosion_right
        explosion.init(position)
        return explosion
      }
      case MapObjectType.Explosion_middle => {
        val explosion = new Explosion_middle
        explosion.init(position)
        return explosion
      }
    }
  }
}
