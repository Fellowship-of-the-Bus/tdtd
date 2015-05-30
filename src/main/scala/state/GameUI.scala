package com.github.fellowship_of_the_bus
package tdtd
package state

import lib.ui.{Button, ToggleButton}
import game.IDMap._
import lib.game.GameConfig
import lib.game.GameConfig.{Width,Height}

import org.newdawn.slick.{GameContainer, Graphics, Color, Input, KeyListener}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import game.Game

import ui._

object GameUI extends BasicGameState {
  object Dimensions {
    val topHeight = 66
    val gaHeight = Height-topHeight
    val gaWidth = 900

    val infoViewWidth = Width-gaWidth

    val mapHeight = gaWidth/2
    val mapWidth = gaWidth/2

    val towerMarketHeight = gaHeight-mapHeight
    val towerMarketWidth = gaWidth/4
  }

  implicit val bgColor = Color.gray
  implicit val game = new Game

  val ui = new Pane(0, 0, Width, Height)
  
  implicit val id = getID

  implicit var input: Input = null
  implicit var SBGame: StateBasedGame = null

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int) = {
    this.game.tick()
  }

  def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    ui.render(gc, sbg, g)
  }

  def init(gc: GameContainer, sbg: StateBasedGame) = {
    import Dimensions._

    input = gc.getInput
    SBGame = sbg
    gc.getGraphics.setBackground(bgColor)

    val top = new StatusBar()
    val gameArea = new GameArea(0, topHeight, gaWidth, gaHeight)
    val infoView = new InfoView()

    ui.addChildren(top, gameArea, infoView)
    ui.init(gc, sbg)
  }

  def getID() = Mode.GameUIID
}
