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

class InfoView(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color, game: Game) extends Pane(x, y, width, height) {
  def this()(implicit bg: Color, game: Game) = this(gaWidth, topHeight, infoViewWidth, gaHeight)

  var currentTower: Option[Tower] = None

  def sell() : Unit = {
  	currentTower match {
  		case Some(t) => {
  			()
  		}
  		case None => ()
  	}
  }

  def upgrade() : Unit = {
  	currentTower match {
  		case Some(t) => {
  			game.upgrade(t)
  		}
  		case None => ()
  	}
  }

  def setRandom() : Unit = {
  	println("Random")
  	currentTower match {
  		case Some(t) => {
  			t.setAI(new RandomAI)
  		}
  		case None => ()
  	}
  }

   def setClosest() : Unit = {
   	println("Closest")
  	currentTower match {
  		case Some(t) => {
  			t.setAI(new ClosestAI)
  		}
  		case None => ()
  	}
  }

  def setClosestGoal() : Unit = {
   	println("ClosestGoal")
  	currentTower match {
  		case Some(t) => {
  			t.setAI(new ClosestToGoalAI)
  		}
  		case None => ()
  	}
  }

  def buyAI() : Unit = {}

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
    val lineWidth = g.getLineWidth()
    val font = g.getFont()
    g.setColor(Color.black)
    g.setLineWidth(2)
    g.drawLine(0, 25, width, 25)

    currentTower match {
    	case Some(t) => {
    		var w = font.getWidth(t.kind.name)
    		g.drawString(t.kind.name, width/2 - w/2, 5)
    		var y = 40
    		for (line <- t.describe()) {
    			g.drawString(line, 5, y)
    			y += 25
    		}
    	}
    	case None => ()
    }

    g.drawString("Select AI", 5, 250)
    g.setLineWidth(lineWidth)
  }

   override def init(gc: GameContainer, sbg: StateBasedGame) = {
		var g = gc.getGraphics()
		val font = g.getFont()
		var w = font.getWidth("Sell")
		var h = font.getHeight("Sell")

		val sellButton = new Button("Sell", width - (w * 1.2f) - 5, 40, w + 5, h + 5, sell)  
		w = font.getWidth("Upgrade")
		h = font.getHeight("Upgrade")
		val upgradeButton = new Button("Upgrade", width - (w * 1.2f) - 5, 225, w + 5, h + 5, upgrade)

		w = font.getWidth("Random")
		h = font.getHeight("Random")
		val randomButton = new Button("Random", 5, 285, w + 5, h + 5, setRandom)

		val oldw = w
		w = font.getWidth("Closest to Tower")
		h = font.getHeight("Closest to Tower")
		val closestButton = new Button("Closest to Tower", oldw + 40, 285, w + 5, h + 5, setClosest)

		w = font.getWidth("Closest to Goal")
		h = font.getHeight("Closest to Goal")
		val closestGoalButton = new Button("Closest to Goal", oldw + 40, 320, w + 5, h + 5, setClosestGoal)

		w = font.getWidth("Buy AI")
		h = font.getHeight("Buy AI")
		val buyAIButton = new Button("Buy AI", oldw + 40, 247.5f, w + 5, h + 5, buyAI)

		addChildren(sellButton)
		addChildren(upgradeButton)
		addChildren(randomButton)
		addChildren(closestButton)
		addChildren(closestGoalButton)
		addChildren(buyAIButton)
	}
}
