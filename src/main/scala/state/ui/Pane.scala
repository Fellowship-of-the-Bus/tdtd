package com.github.fellowship_of_the_bus
package tdtd
package state.ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import game._

class Pane(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, val game: Game) extends UIElement(x, y, width, height) {
  private var children: List[UIElement] = List()

  def addChildren(child: UIElement, childs: UIElement*): Unit = {
    children = child::(childs.toList++children)
  }

  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int) = {
    for (child <- children) {
      child.update(gc, sbg, delta)
    }    
  }

  def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    val linewidth = g.getLineWidth
    g.setLineWidth(3)

    g.setColor(bg)
    g.fillRect(0, 0, width, height)
    g.setColor(Color.black)
    g.drawRect(0, 0, width, height)

    g.setLineWidth(linewidth)
  }

  def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    g.translate(x, y)
    draw(gc, sbg, g)
    for (child <- children) {
      child.render(gc, sbg, g)
    }
    g.translate(-x, -y)
  }

  def init(gc: GameContainer, sbg: StateBasedGame) = {
    for (child <- children) {
      child.init(gc, sbg)
    }
  }
}
