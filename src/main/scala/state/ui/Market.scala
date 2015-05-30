package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}
import lib.ui.{Button, ToggleButton}

import GameUI.Dimensions._
import game._

class Market(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this(x: Float)(implicit bg: Color, game: Game) = this(x, mapHeight, towerMarketWidth, towerMarketHeight)

  val buttonSize = 10f
  var selectedTower =0

  // Set the buttons in a market
  // The code here is more based around getting some images with which it can create buttons rather than actual buttons
  def setButtons( before: Int, buttons: List[Button]) {
  	// placement
  	val spacing = (width - (buttonSize * buttons.length)) / (buttons.length + 1) 
  	var towerSelection = 1 + before
  	var spotX = spacing
  	val spotY = (height - buttonSize) / 2

  	// Need to change these from text buttons to image buttons
  	var nButton: ToggleButton = null

  	// selectedTower would be a variable shared between markets which identifies the currently selected tower
  	// Pressing a button would set the variable, causing that button to appear pressed and allowing placement
  	// Maybe prevent selection based on money? Or just prevent placement?
  	// Reset selected tower to 0 (identifying no tower) when a tower is placed and on press of a cancel button
  	for (i <- 0 until buttons.length) {
  		// commented out because needs implicit input
  		// nButton = ToggleButton("X", spotX, spotY, // "X" is a placeholder
	   //    () => selectedTower = towerSelection + i, // update
	   //    () => (selectedTower == 1)) // query

			// add button to the market (how does addChildren work?)
  		addChildren( nButton, null)

  		// set the placement for next button
			spotX += buttonSize + spacing
  	}


	    
  }
  
  
}
