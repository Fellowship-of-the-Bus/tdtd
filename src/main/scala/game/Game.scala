package com.github.fellowship_of_the_bus
package tdtd
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import IDMap._
import lib.game.GameConfig.{Height,Width}


class Layer
case object BothLayers extends Layer
case object TopLayer extends Layer
case object BottomLayer extends Layer

object Layer {
  import scala.language.implicitConversions
  implicit def layer2Int(l: Layer) = l match {
    case TopLayer => 0
    case BottomLayer => 1
    case _ => -1
  }  
}
import Layer._

class Game {
  private var lives = 10
  private var money = 0
  private var wave = 1
  private var map = Array(new GameMap(), new GameMap())

  private var towers = List[Tower]()
  private var enemies = List[Enemy]()
  private var projectiles = List[Projectile]()
  private var score = 0

  def getMoney = money
  def getScore = score

  var timer = 0
  def tick(): Unit = {
    if (isGameOver) return

    for (t <- towers; if (t.active)) {
      val p = t.tick()
      p match {
        case Some(project) => projectiles = project::projectiles
        case None => ()
      }
    }

    for (e <- enemies; if (e.active)) {
      e.tick()
    }

    for (p <- projectiles; if (p.active)) {
      p.tick()
    }
  }

  def placeTower(tower: Tower, r: Float, c: Float, layer: Layer) = layer match {
    case BothLayers if (map.forall(_.placeable(r, c))) =>
      map.foreach(_.placeTower(r, c, tower))
    case _ if (map(layer).placeable(r,c)) =>
      map(layer).placeTower(r, c, tower)
  }

  def newRoundReady() = enemies.isEmpty

  def sendNextWave() = {
    
  }

  def canSell(r: Float, c: Float, layer: Layer) = map(layer).removable(r, c)

  def sell(r: Float, c: Float, layer: Layer) = {
    val sellValue = layer match {
      case BothLayers => 
        val moneyVal: Int = map.foldLeft(0)((sum, m) => sum + m(r, c).flatMap(_.getTower.map(_.sell)).getOrElse(0))
        map.foreach(_.removeTower(r, c))
        moneyVal
      case _ =>
        val moneyVal: Int = map(layer)(r, c).flatMap(_.getTower.map(_.sell)).getOrElse(0)
        map(layer).removeTower(r, c)
        moneyVal
    }
    money += sellValue
  }

  var isGameOver = false
  def gameOver() = {
    isGameOver = true
  }
}
