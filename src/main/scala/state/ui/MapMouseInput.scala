package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color, Input}
import org.newdawn.slick.state.{StateBasedGame}
import org.newdawn.slick.util.InputAdapter
import org.newdawn.slick.geom.Rectangle


import GameUI.Dimensions._
import game._
import IDMap._
import game.Layer._



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
  var cx = 0
  var cy = 0

  var other: Option[MapInput] = None
  def setOther (mI: MapInput) {
    other = Some(mI)
  }

  var gc: GameContainer = null
  var sbg: StateBasedGame = null

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

  override def mouseDragged(oldx:Int, oldy:Int, newx:Int, newy: Int): Unit = {
    mouseMoved(oldx, oldy, newx, newy)
    mode = MOUSE_DOWN
  }

//  override def mousePressed(button:Int, x:Int, y:Int):Unit = {
//    if (mode == MOUSE_OVER && button == LEFT) {
//      mode = MOUSE_DOWN
//    }
//  }

//  override def mouseReleased(button:Int
  override def mouseReleased(button: Int, x:Int, y:Int): Unit = {
    var actX = x - absX.toInt
    var actY = y - absY.toInt
    var oldc = (cx.toFloat/view.widthRatio).toInt
    var oldr = (cy.toFloat / view.heightRatio).toInt
    var c = (actX.toFloat/view.widthRatio).toInt
    var r = (actY.toFloat / view.heightRatio).toInt
    if (mode == MOUSE_DOWN && inMap(actX,actY) && button == LEFT && ((oldc == c) && (oldr == r))) {
      mx = actX
      my = actY
      mode = MOUSE_CLICK
      if (sbg.getCurrentStateID == Mode.GameUIID) {
        action(r, c)
      }
      if (other.isEmpty) {
      } else {
        other.get.mx = mx
        other.get.my = my
        other.get.onOther = true
      }
    }
  }

  override def mousePressed(button: Int, x:Int, y:Int): Unit = {
    var actX = x - absX.toInt
    var actY = y - absY.toInt
    if (inMap(actX, actY) && button == LEFT) {
      mode = MOUSE_DOWN
      cx = actX
      cy = actY
      mx = actX
      my = actY
      if(!other.isEmpty) {
        other.get.mx = mx
        other.get.my = my
        other.get.onOther = true
      }
    }
  }

  def init(gc: GameContainer, sbg: StateBasedGame) = {
    this.gc = gc
    this.sbg = sbg
  }

  def render(g:Graphics) = {
    val highlightColour = Color.red
    val nmx = (mx/view.widthRatio).toInt*view.widthRatio
    val nmy = ((my)/view.heightRatio).toInt*view.heightRatio

    if (isMouseOver || isMouseClick || onOther || isMouseDown) {    
      if (GameUI.placeSelection != NoTowerID) {
        val t = Tower(GameUI.placeSelection)
        var l = Layer.layer2Int(view.layer)

        def drawRange() {
          val rx = t.range * view.widthRatio
          val ry = t.range * view.heightRatio
          val tx = nmx + 0.5f * view.widthRatio
          val ty = nmy + 0.5f * view.heightRatio

          g.setClip(new Rectangle(absX, absY, width, height))
          g.setColor(new Color(0, 99, 0, 50))
          g.fillOval(tx-rx,ty-ry,rx*2,ry*2)
          g.setColor(new Color(0, 99, 0, 255))
          g.drawOval(tx-rx,ty-ry,rx*2,ry*2)
          g.clearClip()
        }
        
        def drawRanges() {
          GameUI.placeSelection match {
            case TorpedoTowerID => drawRange()

            case CannonTowerID  |
                 MissileTowerID |
                 NetTowerID     |
                 WhirlpoolBottomID =>
                  if (l == 0) {
                    drawRange()
                  }

            case SteamTowerID     |
                 IceTowerBottomID |
                 DepthChargeTowerID =>
                  if (l == 1) {
                    drawRange()
                  }

            case HarpoonTowerID =>
                  if (!onOther) {
                    drawRange()
                  }

            case _ =>
          }
        }

        var image = IDMap.images(GameUI.placeSelection)
        val scaleX = view.widthRatio/image.getWidth
        val scaleY = view.heightRatio/image.getHeight

        def drawSprite() {
          g.scale(scaleX,scaleY)
          image.draw(nmx * 1/scaleX, nmy * 1/scaleY)
          g.scale(1/scaleX, 1/scaleY)
        }

        if ((t.id >= IDMap.EitherTStart && t.id <= IDMap.BothTEnd) ||
            (l == 0 && t.id >= IDMap.TopTStart && t.id <= IDMap.TopTEnd) ||
            (l == 1 && t.id >= IDMap.UnderTStart && t.id <= IDMap.UnderTEnd)) {
          if (!onOther || t.id == OilDrillTowerID) {
            drawSprite()
            drawRanges() 
          }
        } else {
          if (onOther) {
            drawRanges() 
            if (t.id == IceTowerBottomID) {
              var image = IDMap.images(IceTowerTopID)
              drawSprite()
            }
          }
        }
        



        
            
      }
      //println(s"$nmx, $nmy")
      g.setColor(new Color(0,255,0,(0.2*255).asInstanceOf[Int]))
      g.fillRect(nmx, nmy, view.widthRatio.toInt, view.heightRatio.toInt)
    }
  }
}
