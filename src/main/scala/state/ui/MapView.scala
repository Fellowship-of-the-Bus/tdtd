package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._
import game.Layer._
import game.IDMap._
import game.GameMap._

class MapView(x: Float, y: Float, width: Float, height: Float, layer: Layer, gameArea : GameArea)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this(x: Float, y: Float, layer: Layer, gameArea: GameArea)(implicit bg: Color, game: Game) = this(x, y, mapWidth, mapHeight, layer, gameArea)

  val map = game.getMap(layer)
  val mapInput = new MapInput(0,0, width, height, place, this)
  val widthRatio = width / map.mapWidth
  val heightRatio = height / map.mapHeight
  def convert(r: Float, c: Float) = {
    (c * widthRatio, r * heightRatio)
  }

  def convert(go: GameObject): (Float, Float) = {
    val (r,c) = go.topLeftCoord
    convert(r, c)
  }

  def drawObject( e:GameObject, ex: Float, ey: Float,  g:Graphics) {
    var image = IDMap.images(e.id)
    g.scale(e.width*heightRatio/image.getWidth,e.height*widthRatio/image.getHeight)
    IDMap.images(e.id).draw(ex * image.getWidth/e.width/widthRatio, ey*image.getHeight/e.height/heightRatio)
    g.scale(image.getWidth/e.width/widthRatio,image.getHeight/e.height/heightRatio)
  }
 
  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)

    g.setColor(Color.black)
    for (r <- 0 until map.mapHeight) {
      for (c <- 0 until map.mapWidth) {
        val (x, y) = convert(r, c) 
        g.drawLine(x, 0, x, height)
        g.drawLine(0, y, width, y)

        val tile = map(r, c).get
        val enemies = tile.enemies
        for (e <- enemies; if (e.active)) {
          val (ex, ey) = convert(e)
          drawObject(e,ex, ey, g)
        }

        val tower = tile.tower
        if (! tower.isEmpty) {
          val t = tower.get
          val (tx, ty) = convert(t)
          drawObject(t, tx, ty, g)
        }
      }
    }

    for (p <- game.projectiles; if (p.active)) {
      val (px, py) = convert(p)
      IDMap.images(p.id).draw(px, py)
    }
    mapInput.render(g)
 } 
  def place(r:Float, c:Float) {
    var t = gameArea.placeSelection
    if (t.isEmpty) {
      if (map(r,c).isEmpty) {
        gameArea.displaySelection = None
      } else {
        gameArea.displaySelection = map(r,c).get.getTower
      }
    } else {
      if (Layer.layer2Int(layer) == 0) { //topLayer
        t.get.id match {
          case HarpoonTowerID |
               CannonTowerID  |
               DepthChargeTowerID  |
               MissileTowerID |
               NetTowerID => {
            if (game.placeTower (t.get,r,c, layer) == okay){
              gameArea.placeSelection = None
            }
          }
          case OilDrillTowerID => {
            game.placeTower (t.get,r,c,BothLayers)
            gameArea.placeSelection = None
          }
          case _ => ()
            
        }
      }
    }
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    mapInput.setInput(gc.getInput)
    super.init(gc, sbg)
  }
}
