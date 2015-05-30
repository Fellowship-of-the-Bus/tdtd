package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import lib.game.GameConfig.{Width,Height}

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._

class TextBox(x: Float, y: Float, width: Float, height: Float, query: () => String)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
    g.drawString(query(), 2, 0) 
  }
}
