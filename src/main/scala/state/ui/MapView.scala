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

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)

    g.setColor(Color.black)
    for (r <- 0 until map.mapHeight) {
      for (c <- 0 until map.mapWidth) {
        val x = c * width / map.mapWidth
        val y = r * height / map.mapHeight
        g.drawLine(x, 0, x, height)
        g.drawLine(0, y, width, y)
      }
    }
  }

  // override def init(gc: GameContainer, sbg: StateBasedGame) = {
    
  // }
}