package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color, Input}
import org.newdawn.slick.state.{StateBasedGame}
import org.newdawn.slick.util.InputAdapter

import game.IDMap._

class KeyboardInput() extends InputAdapter {
	
	val KeyMap = Map(
		Input.KEY_H -> HarpoonTowerID
	)

	override def setInput(input: Input) = {
		input.addKeyListener(this)
	}

	override def keyPressed(key: Int, c: Char): Unit = {
		if (key == Input.KEY_H) {
			GameUI.placeSelection = HarpoonTowerID
			GameUI.displaySelection = IDSelection(HarpoonTowerID)
		}
	}
}