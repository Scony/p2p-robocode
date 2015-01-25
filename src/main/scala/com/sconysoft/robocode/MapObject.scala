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

  private class Bomb extends MapObject {
    override val destroyable: Boolean = false
    override val movable: Boolean = false
    override val fieldType: MapObjectType = MapObjectType.Bomb
    override val fileName: String = "img/bomb.png"
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
    }
  }
}
