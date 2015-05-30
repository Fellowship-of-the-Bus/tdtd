package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._

class Market(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this(x: Float)(implicit bg: Color, game: Game) = this(x, mapHeight, towerMarketWidth, towerMarketHeight)

  
}
