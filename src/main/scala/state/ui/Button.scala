package com.github.fellowship_of_the_bus
// package lib
// package ui
package tdtd
package state.ui


import org.newdawn.slick.{GameContainer, Graphics, Color, Input}
import org.newdawn.slick.util.InputAdapter
import org.newdawn.slick.gui.MouseOverArea
import org.newdawn.slick.state.{StateBasedGame}

import lib.game.GameConfig

import lib.ui.UIElement


object Button {
  /** width of a button */
  val width = 200
  /** height of a button */
  val height = 20

  def apply(text: String, x: Float, y: Float, action: ()=>Unit)(implicit input: Input, state: Int, game: StateBasedGame) = {
    val b = new Button(text, x, y, width, height, () => if (game.getCurrentStateID == state) action())
    b.setInput(input)
    b
  }

}

class Button(text: String, val x: Float, val y: Float, val width: Float, val height: Float, action: () => Unit)
 extends InputAdapter with UIElement {
  object ButtonMode {
    val NORMAL = 0
    val MOUSE_OVER = 1
    val MOUSE_DOWN = 2
  }
  import ButtonMode._

  def this(text: String, x: Float, y: Float, action: () => Unit) =
    this(text, x, y, 
      GameConfig.graphics.getFont.getWidth(text) + 5,
      GameConfig.graphics.getFont.getHeight(text) + 5,
      action)
  
  private var mode = NORMAL

  def isMouseOver() = mode == MOUSE_OVER
  def isMouseDown() = mode == MOUSE_DOWN

  def inButton(newx: Int, newy: Int): Boolean = 
    x < newx && newx < x+width && y < newy && newy < y+height

  override def setInput(input: Input) = {
    input.addMouseListener(this)
  }

  override def mouseMoved(oldx: Int, oldy: Int, newx: Int, newy: Int): Unit = {
    if (inButton(newx, newy)) {
      mode = MOUSE_OVER
    } else {
      mode = NORMAL
    }
  }

  val LEFT = 0
  override def mousePressed(button: Int, x: Int, y: Int): Unit = {
    if (mode == MOUSE_OVER && button == LEFT) {
      mode = MOUSE_DOWN
    }
  }

  override def mouseReleased(button: Int, x: Int, y: Int): Unit = {
    val in = inButton(x, y)
    if (mode == MOUSE_DOWN && button == LEFT && in) {
      action()
      println("do action")
    }
    if (in) mode = MOUSE_OVER
    else mode = NORMAL
  }

  // def mouseClicked(button: Int, x: Int, y: Int, clickCount: Int): Unit
  // def mouseDragged(oldx: Int, oldy: Int, newx: Int, newy: Int): Unit = ???
  // def mouseWheelMoved(change: Int): Unit = ???

  def render(g: Graphics): Unit = {
    g.translate(x, y)

    val textColor = Color.red
    var bgColor = Color.white

    if (isMouseOver){
      bgColor = bgColor.darker(0.2f)
    } else if (isMouseDown) {
      bgColor = bgColor.darker(0.3f)
    }

    g.setColor(bgColor)
    g.fillRoundRect(0, 0, width, height, 5)
    g.setColor(textColor)

    val xc = width/2-g.getFont.getWidth(text)/2
    val yc = height/2-g.getFont.getHeight(text)/2
    
    g.drawString(text, xc, yc)

    g.translate(-x, -y)
  }

  def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    render(g)
  }

  def init(gc: GameContainer, sbg: StateBasedGame): Unit = ()
  def update(gc: GameContainer, sbg: StateBasedGame, delta: Int): Unit = ()
  def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = render(g)
}
