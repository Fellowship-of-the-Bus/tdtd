package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import lib.game.GameConfig.{Width,Height}
import lib.ui.{Button, Drawable, ImageButton}

import org.newdawn.slick.{GameContainer, Graphics, Color,Input}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._
import game.IDMap._

class WaveBar(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color) extends Pane(x, y, width, height) {
  def this()(implicit bg: Color) = this(0, 0, Width, topHeight)

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
    var wave : Option[Wave]= None
    if( game.enemies.isEmpty ) {
      wave = game.waves.headOption
    } else {
      wave = Some(new Wave(1, 0,0,0,0,0,0,0,   0,0,0,0,0,0,0))
      var enemies = game.enemies
      enemies.map((e) =>
              if (e.active) {
                e.id match {
                  case HippoID => wave.get.enemyNumbers(0) += 1
                  case AlligatorID => wave.get.enemyNumbers(1) += 1
                  case TurtleID => wave.get.enemyNumbers(2) += 1
                  case DolphinID => wave.get.enemyNumbers(3) += 1
                  case PenguinID => wave.get.enemyNumbers(4) += 1
                  case KrakenID => wave.get.enemyNumbers(5) += 1
                  case HydraID => wave.get.enemyNumbers(6) += 1
                  case CrabID => wave.get.enemyNumbers(7) += 1
                  case SquidID => wave.get.enemyNumbers(8) += 1
                  case FishID => wave.get.enemyNumbers(9) += 1
                  case JellyfishID => wave.get.enemyNumbers(10) += 1
                  case SharkID => wave.get.enemyNumbers(11) += 1
                  case WhaleID => wave.get.enemyNumbers(12) += 1
                  case MegalodonID => wave.get.enemyNumbers(13) += 1
                  case _ => ()
                }
              })
      var queue = game.spawnQueueTop
      queue.map((e) =>
              if (e.active) {
                e.id match {
                  case HippoID => wave.get.enemyNumbers(0) += 1
                  case AlligatorID => wave.get.enemyNumbers(1) += 1
                  case TurtleID => wave.get.enemyNumbers(2) += 1
                  case DolphinID => wave.get.enemyNumbers(3) += 1
                  case PenguinID => wave.get.enemyNumbers(4) += 1
                  case KrakenID => wave.get.enemyNumbers(5) += 1
                  case HydraID => wave.get.enemyNumbers(6) += 1
                  case CrabID => wave.get.enemyNumbers(7) += 1
                  case SquidID => wave.get.enemyNumbers(8) += 1
                  case FishID => wave.get.enemyNumbers(9) += 1
                  case JellyfishID => wave.get.enemyNumbers(10) += 1
                  case SharkID => wave.get.enemyNumbers(11) += 1
                  case WhaleID => wave.get.enemyNumbers(12) += 1
                  case MegalodonID => wave.get.enemyNumbers(13) += 1
                  case _ => ()
                }
              })
      queue = game.spawnQueueBottom
      queue.map((e) =>
              if (e.active) {
                e.id match {
                  case HippoID => wave.get.enemyNumbers(0) += 1
                  case AlligatorID => wave.get.enemyNumbers(1) += 1
                  case TurtleID => wave.get.enemyNumbers(2) += 1
                  case DolphinID => wave.get.enemyNumbers(3) += 1
                  case PenguinID => wave.get.enemyNumbers(4) += 1
                  case KrakenID => wave.get.enemyNumbers(5) += 1
                  case HydraID => wave.get.enemyNumbers(6) += 1
                  case CrabID => wave.get.enemyNumbers(7) += 1
                  case SquidID => wave.get.enemyNumbers(8) += 1
                  case FishID => wave.get.enemyNumbers(9) += 1
                  case JellyfishID => wave.get.enemyNumbers(10) += 1
                  case SharkID => wave.get.enemyNumbers(11) += 1
                  case WhaleID => wave.get.enemyNumbers(12) += 1
                  case MegalodonID => wave.get.enemyNumbers(13) += 1
                  case _ => ()
                }
              })
    }
    if (wave.isEmpty) {
//      g.drawString("Final Wave!", width/2 - 50, height/2)
    } else {
      val enemy = wave.get.enemyNumbers
      var n = (enemy.foldLeft(0)((a,b) => if (b != 0) a + 1 else a))
      val blockWidth = (height * 1.5).toInt
      
      var i = 0
      for (j <- 0 to enemy.length-1) {
        if (enemy(j) != 0) {
          var image = IDMap.images(wave.get.enemyIDs(j))
          var scale = (height-10).toFloat/image.getHeight.toFloat
          if (width/blockWidth < n) {
            scale *= n.toFloat * blockWidth.toFloat / width.toFloat
          }
          g.scale(scale, scale)
          image.draw(i*blockWidth / scale, 5/scale)
          g.scale(1/scale,1/scale)
          var num = enemy(j)
          g.drawString( s"x $num", i*blockWidth + height , height/2 )
          i += 1
        } 
        if (j == 6) {
          var lw = g.getLineWidth
          g.setLineWidth(lw*2)
          g.drawLine(width/2-85,0,width/2-85,height)
          g.setLineWidth(lw)
          i = 5
        }
      }
    }
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {

    super.init(gc, sbg)
  }
}
