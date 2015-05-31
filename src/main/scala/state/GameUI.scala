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

import game.Tower

import ui._

class Selection
case class IDSelection(id: Int) extends Selection
case class TowerSelection(t: Tower) extends Selection
case object NoSelection extends Selection

object GameUI extends BasicGameState {
  object Dimensions {
    val topHeight = 66
    val gaHeight = Height-topHeight
    val gaWidth = 980

    val infoViewWidth = Width-gaWidth

    val mapHeight = gaWidth/2
    val mapWidth = gaWidth/2

    val towerMarketHeight = gaHeight-mapHeight
    val towerMarketWidth = gaWidth/4
  }

  implicit val bgColor = Color.gray
  var game: Game = null

  val ui = new Pane(0, 0, Width, Height)
  
  implicit val id = getID

  implicit var input: Input = null
  implicit var SBGame: StateBasedGame = null
  var gc: GameContainer = null

  var placeSelection : Int = NoTowerID
  var displaySelection : Selection = NoSelection

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int) = {
    if (! gc.isPaused()) {
      this.game.tick()  
    }
  }

  def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    ui.render(gc, sbg, g)

    if (game.isGameOver) {
      g.setColor(new Color(255, 0, 0, (0.5 * 255).asInstanceOf[Int]))
      g.fillRect(0, 0, Width, Height)
      images(GameOverID).draw(0, 0)

      g.setColor(Color.white)
    }
  }

  var statusBar: StatusBar = null
  def init(gc: GameContainer, sbg: StateBasedGame) = {
    import Dimensions._

    this.gc = gc

    input = gc.getInput
    SBGame = sbg
    gc.getGraphics.setBackground(bgColor)

    val top = new StatusBar()
    statusBar = top
    val gameArea = new GameArea(0, topHeight, gaWidth, gaHeight)
    val infoView = new InfoView()
    ui.addChildren(top, gameArea, infoView)
    ui.setState(getID)
    ui.init(gc, sbg)
  }

  def newGame() = {
    gc.setPaused(false)
    game = new Game    
    ui.resetGame(game)
    placeSelection = NoTowerID
    displaySelection = NoSelection
  }

  def resumeGame() = {
    gc.setPaused(false)
  }

  def gameInProgress(): Boolean = {
    game != null && ! game.isGameOver
  }

  def getID() = Mode.GameUIID
}
