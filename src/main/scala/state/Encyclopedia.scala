package com.github.fellowship_of_the_bus
package tdtd
package state

import lib.ui.{Button, ToggleButton}
import game.IDMap._
import lib.game.GameConfig
import lib.game.GameConfig.{Width,Height}

import org.newdawn.slick.{GameContainer, Graphics, Color, Input, KeyListener}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

object Encyclopedia extends BasicGameState {
  val centerx = Width/2-Button.width/2
  implicit val id = getID

  implicit var input: Input = null
  implicit var SBGame: StateBasedGame = null
  var gc: GameContainer = null

  lazy val choices = {
    val lst = List(
      Button("Back", centerx, 200+90, () => SBGame.enterState(Mode.MenuID)))
    lst.foreach(_.init(gc, SBGame))
    lst.foreach(_.setState(getID))
    lst
  }

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    implicit val input = gc.getInput
  }

  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    val fotb = images(FotBLogoID)
    fotb.draw(Width/2-fotb.getWidth/2, 3*Height/4)
    for ( item <- choices ) {
      item.render(g)
    }
  }

  def init(gc: GameContainer, game: StateBasedGame) = {
    input = gc.getInput
    this.gc = gc
    SBGame = game
    gc.getGraphics.setBackground(Color.cyan)
  }

  def getID() = Mode.EncyclopediaID
}
