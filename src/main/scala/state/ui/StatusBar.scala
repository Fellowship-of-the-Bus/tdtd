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

  var speedAction: () => Unit = null

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    val buttonWidth = 90
    val buttonHeight = 20

    val lives = new TextBox(5, 15+buttonHeight, buttonWidth, buttonHeight,
      () => s"Lives: ${game.getLives}")

    val waveNum = new TextBox(width-295, 10, buttonWidth-5, buttonHeight,
      () => s"Wave: ${game.getWaveNumber}")

    val money = new TextBox(width-100, 10, buttonWidth, buttonHeight,
      () => s"$$${game.getMoney}")

    val menu = new Button("menu", width-200, 10, buttonWidth, buttonHeight,
      () => {
        gc.setPaused(true)
        (sbg.enterState(Mode.MenuID))
      })

    val sendWave = new Button("send wave", width-200, 15+buttonHeight, buttonWidth, buttonHeight,
      () => game.sendNextWave)
    sendWave.setSelectable(() => game.newRoundReady)

    speed = new ImageButton(images(FastForwardOffID), width-100, 15+buttonHeight, buttonWidth, buttonHeight, null)
    val waveBar = new WaveBar(100, 10, width - 400, height - 20)

    val speedImages = Array(images(FastForwardOffID), images(FastForwardOnID))
    speedAction = () => {
      val spd = game.toggleSpeed
      speed.setImage(speedImages(spd-1))
    }
    speed.setAction(speedAction)
    speed.setSelectable(() => !game.isGameOver)

    addChildren(lives, money, waveNum, sendWave, menu, speed, waveBar)
    super.init(gc, sbg)
  }

  override def reset() = {
    speed.setImage(images(FastForwardOffID))
  }
}
