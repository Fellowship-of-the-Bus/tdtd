package com.github.fellowship_of_the_bus
package tdtd
package state.ui

import org.newdawn.slick.{GameContainer, Graphics, Color, Input, KeyListener}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

abstract class UIElement(x: Int, y: Int, width: Int, height: Int) {
  val topLeft = (x, y)
  val bottomRight = (x+width, y+height)

  def update(gc: GameContainer, game: StateBasedGame, delta: Int): Unit
  def render(gc: GameContainer, game: StateBasedGame, g: Graphics): Unit
  def init(gc: GameContainer, game: StateBasedGame): Unit
}

