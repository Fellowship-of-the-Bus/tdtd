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

class StatusBar(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color) extends Pane(x, y, width, height) {
  def this()(implicit bg: Color) = this(0, 0, Width, topHeight)

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
  }

  // image has to change when game is reset
  private var speed: ImageButton = null

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    val buttonWidth = 90
    val buttonHeight = 20

    val money = new TextBox(width-100, 5, buttonWidth, buttonHeight,
      () => s"$$${game.getMoney}")

    val sendWave = new Button("send wave", width-200, 5, buttonWidth, buttonHeight,
      () => game.sendNextWave)
    sendWave.setSelectable(() => game.newRoundReady)

    val menu = new Button("menu", width-200, 10+buttonHeight, buttonWidth, buttonHeight,
      () => {
        gc.setPaused(true)
        (sbg.enterState(Mode.MenuID))
      })

    speed = new ImageButton(images(FastForwardOffID), width-100, 10+buttonHeight, buttonWidth, buttonHeight, null)
    val waveBar = new WaveBar(100, 10, width - 400, height - 20)

    val speedImages = Array(images(FastForwardOffID), images(FastForwardOnID))
    speed.setAction(() => {
      val spd = game.toggleSpeed
      speed.setImage(speedImages(spd-1))
    })

    addChildren(money, sendWave, menu, speed, waveBar)
    super.init(gc, sbg)
  }

  override def reset() = {
    speed.setImage(images(FastForwardOffID))
  }
}
