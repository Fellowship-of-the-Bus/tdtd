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
  var ui = new Pane(0, 0, Width, Height, Color.gray)
  val game = new Game

  implicit val id = getID

  implicit var input: Input = null
  implicit var SBGame: StateBasedGame = null

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    this.game.tick()
  }

  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    ui.render(gc, game, g)
  }

  def init(gc: GameContainer, game: StateBasedGame) = {
    input = gc.getInput
    SBGame = game
    gc.getGraphics.setBackground(Color.cyan)
  }

  def getID() = Mode.GameUIID
}
