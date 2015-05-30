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

  var placeSelection : Option[Tower] = None
  var displaySelection : Option[Tower] = None
  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    val map1 = new MapView(0, 0, TopLayer,this)
    val map2 = new MapView(mapWidth, 0, BottomLayer,this)
    map1.mapInput.setOther(map2.mapInput)
    map2.mapInput.setOther(map1.mapInput)

    for (i <- 0 until 4) {
      val market = new Market(i*towerMarketWidth)
      addChildren(market)
    }

    addChildren(map1, map2)
    super.init(gc, sbg)
  }
}
