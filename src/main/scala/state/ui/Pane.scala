package com.github.fellowship_of_the_bus
package tdtd
package state.ui

import lib.ui._

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import game._

class Pane(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color) extends AbstractUIElement(x, y, width, height) {
  var children: List[UIElement] = List()
  protected var game: Game = null

  def addChildren(child: UIElement, childs: UIElement*): Unit = {
    val newchildren = child::childs.toList
    for (c <- newchildren) {
      c.absoluteX += absoluteX
      c.absoluteY += absoluteY
    }
    children = newchildren++children
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
      if (child.isVisible()) {
        child.render(gc, sbg, g)
      }
    }
    g.translate(-x, -y)
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    super.init(gc, sbg)
    for (child <- children) {
      child.setState(uiState)
      child.init(gc, sbg)
    }
  }

  def resetGame(g: Game): Unit = {
    game = g
    reset()
    for (c <- children) {
      if (c.isInstanceOf[Pane]) {
        c.asInstanceOf[Pane].resetGame(game)
      }
    }
  }

  def reset(): Unit = ()
}
