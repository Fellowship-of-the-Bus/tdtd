package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._

class MapView(x: Float, y: Float, width: Float, height: Float, layer: Layer)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this(x: Float, y: Float, layer: Layer)(implicit bg: Color, game: Game) = this(x, y, mapWidth, mapHeight, layer)

  val map = game.getMap(layer)

  def convert(r: Float, c: Float) = {
    (c * width / map.mapWidth, r * height / map.mapHeight)
  }

  def convert(go: GameObject): (Float, Float) = {
    val (r,c) = go.topLeftCoord
    convert(r, c)
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
          IDMap.images(e.id).draw(ex, ey)
        }

        val tower = tile.tower
        if (! tower.isEmpty) {
          val t = tower.get
          val (tx, ty) = convert(t)
          IDMap.images(t.id).draw(tx, ty)
        }
      }
    }

    for (p <- game.projectiles; if (p.active)) {
      val (px, py) = convert(p)
      IDMap.images(p.id).draw(px, py)
    }
  }

  // override def init(gc: GameContainer, sbg: StateBasedGame) = {
    
  // }
}