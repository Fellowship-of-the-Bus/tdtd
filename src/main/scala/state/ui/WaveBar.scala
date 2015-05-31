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
    var wave = game.waves.headOption
    if (wave.isEmpty) {
      g.drawString("Final Wave!", width/2 - 50, height/2)
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
      }
    }
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {

    super.init(gc, sbg)
  }
}
