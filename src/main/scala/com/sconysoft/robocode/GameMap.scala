package com.sconysoft.robocode

import java.awt._
import javax.swing.{JFrame, JPanel}

import scala.collection.mutable.ArrayBuffer

class NewCanvas(maxSize: Int) extends Canvas {
  setPreferredSize(new Dimension(maxSize, maxSize))
}

class GameMap (size: Int) {
  val mapSize: Int = validateMapSize(size)
  var objects: ArrayBuffer[MapObject] = generateMap (mapSize)
  val elementSize: Int = 20

  val frame: JFrame = new JFrame ()
  frame.setPreferredSize(new Dimension(mapSize * elementSize + 9, mapSize * elementSize + 28))
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

  val panel: JPanel = new JPanel()
  panel.setBackground(Color.lightGray)
  panel.setSize(new Dimension(mapSize * elementSize, mapSize * elementSize))
  panel.setBounds(0, 0, mapSize * elementSize, mapSize * elementSize)
  val layout: GridBagLayout = new GridBagLayout()

  panel.setLayout(layout)

  frame.getContentPane.setLayout(null)
  frame.getContentPane().add(panel)
  frame.pack()

  frame.setVisible(true)

  def getMapSize (): Int = {
    return mapSize
  }

  def validateMapSize(mapSize: Int): Int = {
    val minSize: Int = 9

    if (mapSize < minSize) minSize else if (mapSize % 2 == 0) mapSize + 1 else mapSize
  }

  def setPlayer (position: Position): Unit = {
    objects.update(position.y * mapSize + position.x, MapObject.getObject(MapObjectType.Ghost, new Position(position.x, position.y)))

    val component = panel.getComponentAt(position.x * elementSize, position.y * elementSize)
    panel.remove(component)

    objects(position.y * mapSize + position.x).draw(panel)
  }

  def generateMap (mapSize: Int): ArrayBuffer[MapObject] = {
    val size: Int = mapSize * mapSize
    var map: ArrayBuffer[MapObject] = new ArrayBuffer[MapObject](size)

    for (x: Int <- 0 to mapSize - 1)
      for (y: Int <- 0 to mapSize - 1)
        map.append(MapObject.getObject (MapObjectType.Grass, new Position(x, y)))

    // generate walls and grass
    for (x: Int <- 0 to mapSize - 1)
      map.update(x, MapObject.getObject(MapObjectType.Wall, new Position(x, 0)))

    for (y: Int <- 1 to mapSize - 1)
      map.update(y * mapSize, MapObject.getObject (MapObjectType.Wall, new Position(0, y)))

    for (x: Int <- 1 to mapSize - 1)
      map.update(size - mapSize + x, MapObject.getObject (MapObjectType.Wall, new Position(x, mapSize - 1)))

    for (y: Int <- 1 to mapSize - 2)
      map.update(mapSize * y + mapSize - 1, MapObject.getObject (MapObjectType.Wall, new Position(mapSize - 1, y)))

    for (x: Int <- 1 to mapSize - 2)
      for (y: Int <- 1 to mapSize - 2)
        if (x % 2 == 0 && y % 2 == 0)
          map.update(y * mapSize + x, MapObject.getObject (MapObjectType.Wall, new Position(x, y)))

    return map
  }

  def initializeGraphics () = {
    objects.foreach(e => e.draw(panel))

    frame.revalidate()
    frame.getContentPane.revalidate()
    panel.revalidate()

    panel.repaint()
    frame.repaint()
  }

  def refresh () = {
    frame.revalidate()
    frame.getContentPane.revalidate()
    panel.revalidate()

    panel.repaint()
    frame.repaint()
  }

}
