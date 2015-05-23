package com.github.fellowship_of_the_bus
package tdtd
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import IDMap._
import lib.game.GameConfig.{Height,Width}

class Game {
  var counter = 0

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    implicit val input = gc.getInput

  }

  var isGameOver = false
  def gameOver() = {
    isGameOver = true

  }
}
