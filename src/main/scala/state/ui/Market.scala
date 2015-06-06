package com.github.fellowship_of_the_bus
package tdtd
package state
package ui


import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}
import lib.ui.{Button, Drawable, ImageButton}

import GameUI.Dimensions._
import game._
import IDMap._
import Tower._

class Market(val parent: GameArea, val towers: List[Int], x: Float, y: Float, width: Float, height: Float)(implicit bg: Color) extends Pane(x, y, width, height) {
  def this(parent: GameArea, towers: List[Int], x: Float)(implicit bg: Color) = this(parent, towers, x, mapHeight, towerMarketWidth, towerMarketHeight)
  
  val buttonSize = 50f

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
      super.draw(gc,sbg,g)
      val spacing = (width - (buttonSize * towers.length)) / (towers.length + 1) 
      var spotX = spacing + 10
      val spotY = (height - buttonSize) / 2 - 20
      for (id <- towers) {
        val tower = Tower(id)
        val c = g.getColor()
        g.drawString(s"$$${tower.value}", spotX, spotY + 75)
        spotX += buttonSize + spacing
      }
  }

  // Set the buttons in a market
  override def init(gc: GameContainer, sbg: StateBasedGame) {
  	val spacing = (width - (buttonSize * towers.length)) / (towers.length + 1) 
  	var spotX = spacing
  	val spotY = (height - buttonSize) / 2

  	// Need to change these from text buttons to image buttons
  	var nButton: Button = null

  	def selectTower(id: Int) {
  		val price = Tower(id).value
      var id2 = id
  		if (GameUI.placeSelection != id) {
          GameUI.placeSelection = id
          if (id == WhirlpoolBottomID) id2 = WhirlpoolTopID
  				GameUI.displaySelection = IDSelection(id2)
  		} else {
  			GameUI.placeSelection = 0
  			GameUI.displaySelection = NoSelection
  		}
  	}

  	for (id <- towers) {
      val tower = Tower(id)
  		nButton = new ImageButton(images(id).copy, spotX, spotY, 50, 50,
	     () => selectTower(id)).setSelectable(() => tower.value <= game.getMoney)
  		addChildren( nButton)
			spotX += buttonSize + spacing
  	}

    super.init(gc, sbg)
  }  
}
