package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color, Input}
import org.newdawn.slick.state.{StateBasedGame}
import org.newdawn.slick.util.InputAdapter


import GameUI.Dimensions._
import game._


class MapInput( x: Float, y:Float, width: Float, height:Float, action: (Float, Float) => Unit, view: MapView )
 extends InputAdapter {
  object MouseMode {
    val NORMAL = 0
    val MOUSE_OVER = 1
    val MOUSE_DOWN = 2
    val MOUSE_CLICK = 3
  }
  println(s"$x,$y,$width,$height")
  import MouseMode._
  var mx = 0
  var my = 0
  val LEFT = 0

  var mode = NORMAL
  def isMouseOver() = mode == MOUSE_OVER
  def isMouseDown() = mode == MOUSE_DOWN
  def isMouseClick() = mode == MOUSE_CLICK

  def inMap(newx: Int, newy: Int) = {
    x < newx && newx < x+width && y < newy && newy < y+height
  }

  override def setInput( input: Input) = {
    input.addMouseListener(this)
  }

  override def mouseMoved(oldx:Int, oldy:Int, newx:Int, newy: Int): Unit = {
    println(s"$newx, $newy")
    mx = newx
    my = newy
    if (inMap(newx, newy)) {
      mode = MOUSE_OVER
    } else {
      mode = NORMAL
    }
  }

//  override def mousePressed(button:Int, x:Int, y:Int):Unit = {
//    if (mode == MOUSE_OVER && button == LEFT) {
//      mode = MOUSE_DOWN
//    }
//  }

//  override def mouseReleased(button:Int
  override def mouseClicked(button: Int, x:Int, y:Int, clickCount:Int): Unit = {
    mx = x
    my = y
    if (inMap(x,y) && button == LEFT && clickCount == 1) {
      mode = MOUSE_CLICK
      action(mx, my)
    }
  }



  def render(g:Graphics) = {
    val highlightColour = Color.red

    if (isMouseOver || isMouseClick) {
      g.setColor(new Color(255,0,0,(0.5*255).asInstanceOf[Int]))
      g.fillRect(x/view.widthRatio, y/view.widthRatio, view.widthRatio.toInt, view.heightRatio.toInt)
    }
  }
}
