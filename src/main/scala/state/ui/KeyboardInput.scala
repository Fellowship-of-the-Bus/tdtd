package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color, Input}
import org.newdawn.slick.state.{StateBasedGame}
import org.newdawn.slick.util.InputAdapter

import game.IDMap._
import game._


class KeyboardInput(var game: Game, gc: GameContainer, sbg: StateBasedGame) extends InputAdapter {
	
	val KeyMap = Map(
	  Input.KEY_H -> HarpoonTowerID,
		Input.KEY_C -> CannonTowerID,
		Input.KEY_T -> TorpedoTowerID,
		Input.KEY_O -> OilDrillTowerID,
		Input.KEY_I -> IceTowerBottomID,
		Input.KEY_D -> DepthChargeTowerID,
		Input.KEY_W -> WhirlpoolBottomID,
		Input.KEY_M -> MissileTowerID,
		Input.KEY_N -> NetTowerID,
		Input.KEY_S -> SteamTowerID
	)

	override def setInput(input: Input) = {
		input.addKeyListener(this)
	}

	override def keyPressed(key: Int, c: Char): Unit = {
			if (sbg.getCurrentStateID == Mode.GameUIID) {
			if (KeyMap.contains(key)) {
				val id = KeyMap(key)
				val t = Tower(id)
				if (game.getMoney >= t.value) {
					GameUI.placeSelection = id
					GameUI.displaySelection = IDSelection(id)
				}
			} else if (key == Input.KEY_SPACE) {
				if (game.newRoundReady) {
					game.sendNextWave
				}
			} else if (key == Input.KEY_F) {
				GameUI.statusBar.speedAction()
			} else if (key == Input.KEY_ESCAPE) {
				gc.setPaused(true)
        		(sbg.enterState(Mode.MenuID))
			}
		}
	}
}