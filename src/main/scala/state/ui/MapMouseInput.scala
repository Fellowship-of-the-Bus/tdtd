package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color, Input}
import org.newdawn.slick.state.{StateBasedGame}
import org.newdawn.slick.util.InputAdapter


import GameUI.Dimensions._
import game._


class MapInput( x: Float, y:Float, width: Float, height:Float, action: (Float, Float) => Unit, view: MapView, absX: Float, absY: Float)
 extends InputAdapter {
  object MouseMode {
    val NORMAL = 0
    val MOUSE_OVER = 1
    val MOUSE_DOWN = 2
    val MOUSE_CLICK = 3
  }
  import MouseMode._
  var mx = 0
  var my = 0
  val LEFT = 0
  var onOther = false

  var other: Option[MapInput] = None
  def setOther (mI: MapInput) {
    other = Some(mI)
  }

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
    var x = newx - absX.toInt
    var y = newy - absY.toInt
   if (inMap(x, y)) {
     mx = x
     my = y
     mode = MOUSE_OVER
     if (other.isEmpty) {
     } else {
       other.get.mx = mx
       other.get.my = my
       other.get.onOther = true
     }
    } else {
      mode = NORMAL
      other.get.onOther = false
    }
  }

//  override def mousePressed(button:Int, x:Int, y:Int):Unit = {
//    if (mode == MOUSE_OVER && button == LEFT) {
//      mode = MOUSE_DOWN
//    }
//  }

//  override def mouseReleased(button:Int
  override def mouseClicked(button: Int, x:Int, y:Int, clickCount:Int): Unit = {
    var actX = x - absX.toInt
    var actY = y - absY.toInt
    if (inMap(actX,actY) && button == LEFT && clickCount == 1) {
      mx = actX
      my = actY
      mode = MOUSE_CLICK
      action(mx, my)
      if (other.isEmpty) {
      } else {
        other.get.mx = mx
        other.get.my = my
        other.get.onOther = true
      }
    }
  }



  def render(g:Graphics) = {
    val highlightColour = Color.red

    if (isMouseOver || isMouseClick || onOther) {
      g.setColor(new Color(255,0,0,(0.1*255).asInstanceOf[Int]))
      g.fillRect((mx/view.widthRatio).toInt*view.widthRatio, 
                 ((my)/view.heightRatio).toInt*view.heightRatio, view.widthRatio.toInt, view.heightRatio.toInt)
    }
  }
}
