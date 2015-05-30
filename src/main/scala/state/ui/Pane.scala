package com.github.fellowship_of_the_bus
package tdtd
package state.ui

import org.newdawn.slick.{GameContainer, Graphics}
import org.newdawn.slick.state.{StateBasedGame}

class Pane(x: Int, y: Int, width: Int, height: Int) extends UIElement(x, y, width, height) {
  var children: List[UIElement] = List()

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    for (child <- children) {
      child.update(gc, game, delta)
    }    
  }

  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    for (child <- children) {
      child.render(gc, game, g)
    }
  }

  def init(gc: GameContainer, game: StateBasedGame) = {
    for (child <- children) {
      child.init(gc, game)
    }
  }
}
