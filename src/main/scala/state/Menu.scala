package com.github.fellowship_of_the_bus
package tdtd
package state

import lib.slick2d.ui.{Button, ToggleButton, UIElement}
import game.IDMap._
import lib.game.GameConfig
import lib.game.GameConfig.{Width,Height}

import org.newdawn.slick.{GameContainer, Graphics, Color, Input, KeyListener}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

trait MenuState extends BasicGameState {
  val centerx = Width/2-Button.width/2
  val startY = 200
  val padding = 30

  implicit val id = getID

  implicit var input: Input = null
  implicit var SBGame: StateBasedGame = null
  var gc: GameContainer = null

  def choices: List[UIElement]

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = ()

  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    val fotb = images(FotBLogoID)
    val background = images(BackgroundID)
    val logo = images(GameLogoID)

    background.draw(0, 0)
    fotb.draw(Width/2-fotb.getWidth/2, 3*Height/4)
    logo.draw(Width/2-logo.getWidth/2, Height/8)
    for ( item <- choices ) {
      item.render(gc, game, g)
    }
  }

  def init(gc: GameContainer, game: StateBasedGame) = {
    input = gc.getInput
    this.gc = gc
    SBGame = game
    gc.getGraphics.setBackground(Color.cyan)
  }
}

object Menu extends MenuState {
  lazy val choices = {
    val lst = List(
      Button("New Game", centerx, startY, () => {
        GameUI.newGame
        SBGame.enterState(Mode.GameUIID)
      }),
      Button("Resume Game", centerx, startY+padding, () => {
        GameUI.resumeGame
        SBGame.enterState(Mode.GameUIID)
      }).setSelectable(() => GameUI.gameInProgress),
      Button("Options", centerx, startY+2*padding, () => SBGame.enterState(Mode.OptionsID)),
      // Button("Encyclopedia", centerx, startY+2*padding, () => SBGame.enterState(Mode.EncyclopediaID)),
      Button("Quit", centerx, startY+3*padding, () => System.exit(0)))
    lst.foreach(_.init(gc, SBGame))
    lst.foreach(_.setState(getID))
    lst
  }

  def getID() = Mode.MenuID
}

object Options extends MenuState {
  lazy val choices = List(
    ToggleButton("Display Lifebars", centerx, startY,
      () => GameConfig.showLifebars = !GameConfig.showLifebars, // update
      () => GameConfig.showLifebars), // query
    ToggleButton("Show FPS", centerx, startY+padding,
      () => gc.setShowFPS(! gc.isShowingFPS()), // update
      () => gc.isShowingFPS()), // query
    Button("Back", centerx, startY+2*padding, () => SBGame.enterState(Mode.MenuID)))

  def getID() = Mode.OptionsID
}
