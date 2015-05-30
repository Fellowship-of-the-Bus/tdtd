package com.github.fellowship_of_the_bus
package tdtd
package state.ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

class Pane(x: Float, y: Float, width: Float, height: Float, bg: Color) extends UIElement(x, y, width, height) {
  var children: List[UIElement] = List()

  def update(gc: GameContainer, game: StateBasedGame, delta: Int) = {
    for (child <- children) {
      child.update(gc, game, delta)
    }    
  }

  def draw(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    g.setColor(bg)
    g.fillRect(0, 0, width, height)
    g.setColor(Color.black)
    g.drawRect(0, 0, width, height)
  }

  def render(gc: GameContainer, game: StateBasedGame, g: Graphics) = {
    g.translate(x, y)
    draw(gc, game, g)
    for (child <- children) {
      child.render(gc, game, g)
    }
    g.translate(-x, -y)
  }

  def init(gc: GameContainer, game: StateBasedGame) = {
    for (child <- children) {
      child.init(gc, game)
    }
  }
}
