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

object GameUI extends BasicGameState {
  val game = new Game

  implicit val id = getID

  implicit var input: Input = null
  implicit var SBGame: StateBasedGame = null

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    implicit val input = gc.getInput
  }

  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {

  }

  def init(gc: GameContainer, game: StateBasedGame) = {
    input = gc.getInput
    SBGame = game
    gc.getGraphics.setBackground(Color.cyan)
  }

  def getID() = Mode.GameUIID
}
