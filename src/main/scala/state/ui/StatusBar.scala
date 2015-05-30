package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import lib.game.GameConfig.{Width,Height}
import lib.ui.Button

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._

class StatusBar(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this()(implicit bg: Color, game: Game) = this(0, 0, Width, topHeight)

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    val money = new TextBox(width-100, 5, 90, 20,
      () => s"$$${game.getMoney}")

    val sendWave = new Button("send wave", width-200, 5, 90, 20,
      () => game.sendNextWave)

    addChildren(money, sendWave)
  }
}
