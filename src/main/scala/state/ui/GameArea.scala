package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._
import IDMap._

class GameArea(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color) extends Pane(x, y, width, height) {
  def this()(implicit bg: Color) = this(0, topHeight, gaWidth, gaHeight)
  val keyboard = new KeyboardInput()
  
  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    val map1 = new MapView(0, 0, TopLayer,this)(new Color(100, 100, 255, 210))
    val map2 = new MapView(mapWidth, 0, BottomLayer,this)(new Color(0x11, 0xcc, 0xcc, 255))

    for (i <- 0 until 4) {
      var ids = List[Int]()
      for (k <- TIDRanges(2*i) to TIDRanges((2*i)+1)) {
        ids = k :: ids
      }

      val market = new Market(this, ids, i*towerMarketWidth)
      addChildren(market)
    }

    addChildren(map2, map1)

    super.init(gc, sbg)
    map1.mapInput.setOther(map2.mapInput)
    map2.mapInput.setOther(map1.mapInput)
    keyboard.setInput(gc.getInput)
  }
}
