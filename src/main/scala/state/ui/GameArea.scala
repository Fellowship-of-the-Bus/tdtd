package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._

class GameArea(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this()(implicit bg: Color, game: Game) = this(0, topHeight, gaWidth, gaHeight)

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    val map1 = new MapView(0, 0, TopLayer)
    val map2 = new MapView(mapWidth, 0, BottomLayer)

    for (i <- 0 until 4) {
      val market = new Market(i*towerMarketWidth)
      addChildren(market)
    }

    addChildren(map1, map2)
  }
}