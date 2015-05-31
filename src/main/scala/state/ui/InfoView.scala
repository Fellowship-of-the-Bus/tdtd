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

class InfoView(x: Float, y: Float, width: Float, height: Float)(implicit bg: Color) extends Pane(x, y, width, height) {
  def this()(implicit bg: Color) = this(gaWidth, topHeight, infoViewWidth, gaHeight)

  def sell() : Unit = {
  	GameUI.displaySelection match {
  		case TowerSelection(t) => {
        var layer: Layer = BottomLayer
        println(t.kind.id)
  			t.kind.id match {
          case IceTowerBottomID |
          IceTowerTopID |
          WhirlpoolBottomID |
          WhirlpoolTopID |
          OilDrillTowerID => {
            layer = BothLayers
          }
          case DepthChargeTowerID => {
            layer = TopLayer
          }
          case _ => {
            val map = t.getMap
            if (game.map(TopLayer) == map) {
              layer = TopLayer
            } else {
              layer = BottomLayer
            }
          }
        }
          println(layer)
          game.sell(t.r, t.c, layer)
          GameUI.displaySelection = NoSelection
        }
  		
  		case _ => ()
  	}
  }

  def upgrade() : Unit = {
  	GameUI.displaySelection match {
  		case TowerSelection(t) => {
  			game.upgrade(t)
  		}
  		case _ => ()
  	}
  }

  def setRandom() : Unit = {
  	println("Random")
  	GameUI.displaySelection match {
  		case TowerSelection(t) => {
  			t.setAI(new RandomAI)
  		}
  		case _ => ()
  	}
  }

   def setClosest() : Unit = {
   	println("Closest")
  	GameUI.displaySelection match {
  		case TowerSelection(t) => {
  			t.setAI(new ClosestAI)
  		}
  		case _ => ()
  	}
  }

  def setClosestGoal() : Unit = {
   	println("ClosestGoal")
  	GameUI.displaySelection match {
  		case TowerSelection(t) => {
  			t.setAI(new ClosestToGoalAI)
  		}
  		case _ => ()
  	}
  }

  def buyAI() : Unit = {}

  override def render(gc: GameContainer, sbg: StateBasedGame, g: Graphics) = {
    GameUI.displaySelection match {
      case TowerSelection(t) => super.render(gc, sbg, g)
      case IDSelection(id) => {
        g.translate(x,y)
        draw(gc, sbg, g)
        g.translate(-x,-y)
      }
      case _ => ()
    }
  }

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)
    val lineWidth = g.getLineWidth()
    val font = g.getFont()
    g.setColor(Color.black)
    g.setLineWidth(2)
    g.drawLine(0, 25, width, 25)

    GameUI.displaySelection match {
        case IDSelection(id) => {
          var t = Tower(id)
          var w = font.getWidth(t.name)
          g.drawString(t.name, width/2 - w/2, 5)
          var y = 40
          for (line <- t.describe()) {
            g.drawString(line, 5, y)
            y += 25
          }
        }
        case TowerSelection(t) => {
          var w = font.getWidth(t.kind.name)
          g.drawString(t.kind.name, width/2 - w/2, 5)
          var y = 40
          for (line <- t.describe()) {
            g.drawString(line, 5, y)
            y += 25
          }
        }
        case NoSelection => {}
      }

    g.drawString("Select AI", 5, 250)
    g.setLineWidth(lineWidth)
  }

   override def init(gc: GameContainer, sbg: StateBasedGame): Unit = {
		var g = gc.getGraphics()
		val font = g.getFont()
		var w = font.getWidth("Sell for 50%")
		var h = font.getHeight("Sell fot 50%")

		val sellButton = new Button("Sell for 50%", width - w - 10, 40, w + 5, h + 5, sell)  
		/*w = font.getWidth("Upgrade")
		h = font.getHeight("Upgrade")
		val upgradeButton = new Button("Upgrade", width - (w * 1.2f) - 5, 225, w + 5, h + 5, upgrade)
*/
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
		//addChildren(upgradeButton)
		addChildren(randomButton)
		addChildren(closestButton)
		addChildren(closestGoalButton)
		addChildren(buyAIButton)
    super.init(gc, sbg)
	}

  override def reset() = {
    val t = Tower(HarpoonTowerID, 6,6)
    game.placeTower(t, 6,6, BottomLayer)

    game.money += 500
  }
}
