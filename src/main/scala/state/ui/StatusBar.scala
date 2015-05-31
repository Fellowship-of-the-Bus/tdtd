package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import lib.game.GameConfig.{Width,Height}
import lib.ui.{Button, Drawable, ImageButton}

import org.newdawn.slick.{GameContainer, Graphics, Color,Input}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._
import game.IDMap._

class StatusBar(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this()(implicit bg: Color, game: Game) = this(0, 0, Width, topHeight)

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
  }

  def toggleImage(im1: Drawable, im2: Drawable) = {
    var i = 0
    def toggler() = {
      i = (i + 1) % 2
      if (i == 0) im1
      else im2
    }
    toggler _
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

    val toggler = toggleImage(images(FastForwardOffID), images(FastForwardOnID))
    val speed = new ImageButton(images(FastForwardOffID), width-100, 10+buttonHeight, buttonWidth, buttonHeight, null)
    val waveBar = new WaveBar(100, 10, width - 400, height - 20)
    speed.setAction(() => {
      speed.setImage(toggler())
      game.toggleSpeed
    })

    addChildren(money, sendWave, menu, speed,waveBar)
    super.init(gc, sbg)
  }
}
