package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import lib.game.GameConfig.{Width,Height}
// import lib.ui.Button

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
    val buttonWidth = 90
    val buttonHeight = 20

    val money = new TextBox(width-100, 5, buttonWidth, buttonHeight,
      () => s"$$${game.getMoney}")

    val sendWave = new Button("send wave", width-200, 5, buttonWidth, buttonHeight,
      () => game.sendNextWave)

    val menu = new Button("menu", width-200, 10+buttonHeight, buttonWidth, buttonHeight,
      () => ())

    val speed = new Button("go fast", width-100, 10+buttonHeight, buttonWidth, buttonHeight,
      () => game.toggleSpeed)

    addChildren(money, sendWave, menu, speed)
  }
}
