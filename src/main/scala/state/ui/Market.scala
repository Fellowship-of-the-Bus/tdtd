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

class Market(val parent: GameArea, val towers: List[Int], x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this(parent: GameArea, towers: List[Int], x: Float)(implicit bg: Color, game: Game) = this(parent, towers, x, mapHeight, towerMarketWidth, towerMarketHeight)

  val buttonSize = 50f

  // Set the buttons in a market
  override def init(gc: GameContainer, sbg: StateBasedGame) {
  	val spacing = (width - (buttonSize * towers.length)) / (towers.length + 1) 
  	var spotX = spacing
  	val spotY = (height - buttonSize) / 2

  	// Need to change these from text buttons to image buttons
  	var nButton: Button = null

  	def selectTower(id: Int) {
  		val price = Tower(id).value

  		if (parent.placeSelection != id) {
  			if (game.money > price) {
  				parent.placeSelection = id
  				parent.displaySelection = id
  			}
  		} else {
  			parent.placeSelection = 0
  			parent.displaySelection = 0
  		}
  	}

  	for (id <- towers) {
      nButton = new Button("" + id, spotX, spotY, 50, 50,
        () => selectTower(id))
  		//nButton = new ImageButton(images(id), spotX, spotY, 20, 20,
	    //  () => selectTower(id))
  		addChildren( nButton)
			spotX += buttonSize + spacing
  	}

    super.init(gc, sbg)
  }  
}
