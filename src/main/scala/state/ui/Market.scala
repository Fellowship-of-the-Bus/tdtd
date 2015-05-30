package com.github.fellowship_of_the_bus
package tdtd
package state
package ui


import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}
import lib.ui.Button

import GameUI.Dimensions._
import game._
import IDMap._

class Market(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this(x: Float)(implicit bg: Color, game: Game) = this(x, mapHeight, towerMarketWidth, towerMarketHeight)

  val buttonSize = 10f
  var selectedTower = 0

  // Set the buttons in a market
  def setButtons(towers: List[Int]) {
  	// placement
  	val spacing = (width - (buttonSize * towers.length)) / (towers.length + 1) 
  	var spotX = spacing
  	val spotY = (height - buttonSize) / 2

  	// Need to change these from text buttons to image buttons
  	var nButton: Button = null

  	// selectedTower would be a variable shared between markets which identifies the currently selected tower
  	// Pressing a button would set the variable, causing that button to appear pressed and allowing placement
  	// Maybe prevent selection based on money? Or just prevent placement?
  	// Reset selected tower to 0 (identifying no tower) when a tower is placed and on press of a cancel button
  	for (id <- towers) {
  		//commented out because needs implicit input
  		nButton = new Button("X", spotX, spotY, // "X" is a placeholder
	      () => selectedTower = id)
  		addChildren( nButton)
			spotX += buttonSize + spacing
  	}
  }  
}
