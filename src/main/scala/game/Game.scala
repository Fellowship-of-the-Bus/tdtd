package com.github.fellowship_of_the_bus
package tdtd
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import IDMap._
import lib.game.GameConfig.{Height,Width}
import scala.collection.mutable.{Queue, LinkedList}

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

class Wave (
  val id : Int,
          
  val hippoN : Int,
  val alligatorN : Int,
  val turtleN : Int,
  val dolphinN : Int,
  val penguinN : Int,
  val krakenN : Int,
  val hydraN : Int,

  val crabN : Int,
  val squidN : Int,
  val fishN : Int,
  val jellyfishN : Int,
  val sharkN : Int,
  val whaleN : Int,
  val megalodonN : Int) {

  val enemyNumbers = 
    Array(hippoN, alligatorN, turtleN, dolphinN, penguinN, krakenN, hydraN,
      crabN, squidN, fishN, jellyfishN, sharkN, whaleN, megalodonN)
  val enemyIDs = Array (HippoID, AlligatorID, TurtleID, DolphinID,PenguinID,KrakenID,HydraID,
                  CrabID,SquidID,FishID,JellyfishID,SharkID,WhaleID,MegalodonID)
}


class Game {
  val numSpeeds = 2
  private var numTicks = 1

  private var lives = 10
  var money = 0
  private var waveNumber = 1
  var map = Array(new GameMap(), new GameMap())

  private var towers = List[Tower]()
  private var enemies = List[Enemy]()
  var projectiles = List[Projectile]()
  var waves = new Queue[Wave]()
  var spawnQueue = LinkedList[Enemy]()
  waves.enqueue(new Wave(1,   0,0,0,0,0,0,0,     0,0,5,0,0,0,0))
  waves.enqueue(new Wave(2,   0,2,3,0,0,0,6,     20,0,5,0,5,1,0))
  
  private var spawnRate = 20
  private var timeToSpawn = 0


  private var score = 0

  def getMoney = money
  def getScore = score

  var cleanUpPeriod = 120
  var timer = 0
  def cleanup() = {
    towers = towers.filter(_.active)
    enemies = enemies.filter(_.active)
    projectiles = projectiles.filter(_.active)
  }
                                      
  def tick(): Unit = {
    if (timer == 0) {
      cleanup
      timer = cleanUpPeriod
    } else {
       timer -= 1
    }
    for (i <- 0 until numTicks) {
      if (isGameOver) return

      if (newRoundReady) sendNextWave
      if (!spawnQueue.isEmpty) spawn

      for (t <- towers; if (t.active)) {
        val p = t.tick()
        projectiles = p ++ projectiles
      }

      for (e <- enemies; if (e.active)) {
        e.tick()
      }

      for (p <- projectiles; if (p.active)) {
        p.tick()
      }
    }      
  }

  def spend(cost:Int) = {
    money -= cost
  }

  def addTowerToList( t: Tower) = {
    towers = t :: towers
  }

  def placeTower(tower: Tower, r: Float, c: Float, layer: Layer) = layer match {
    case BothLayers =>
      val res1 = map(TopLayer).placeable(r, c)
      val res2 = map(BottomLayer).placeable(r, c)

      if (res1 == GameMap.okay && res2 == GameMap.okay) {
        map.foreach(_.placeTower(r, c, tower))        
        money = money - tower.kind.value
        towers = tower :: towers
        GameMap.okay
      } else GameMap.semiOccupied

    case _ =>
      val res = map(layer).placeable(r,c)
      if (res == GameMap.okay) {
        map(layer).placeTower(r, c, tower)
        money = money - tower.kind.value
        towers = tower :: towers
      }
      res
  }

  def newRoundReady() = enemies.isEmpty

  def sendNextWave() = {
    var w = waves.dequeue()
    for ( i <- 0 to 13) {
      for (j <- 0 to w.enemyNumbers(i)-1) {
        i match {
          case 0 => spawnQueue = spawnQueue :+ Enemy(HippoID, waveNumber)
          case 1 => spawnQueue = spawnQueue :+ Enemy(AlligatorID, waveNumber) 
          case 2 => spawnQueue = spawnQueue :+ Enemy(TurtleID, waveNumber) 
          case 3 => spawnQueue = spawnQueue :+ Enemy(DolphinID, waveNumber) 
          case 4 => spawnQueue = spawnQueue :+ Enemy(PenguinID, waveNumber) 
          case 5 => spawnQueue = spawnQueue :+ Enemy(KrakenID, waveNumber) 
          case 6 => spawnQueue = spawnQueue :+ Enemy(HydraID, waveNumber) 
          case 7 => spawnQueue = spawnQueue :+ Enemy(CrabID, waveNumber) 
          case 8 => spawnQueue = spawnQueue :+ Enemy(SquidID, waveNumber) 
          case 9 => spawnQueue = spawnQueue :+ Enemy(FishID, waveNumber) 
          case 10 => spawnQueue = spawnQueue :+ Enemy(JellyfishID, waveNumber) 
          case 11 => spawnQueue = spawnQueue :+ Enemy(SharkID, waveNumber) 
          case 12 => spawnQueue = spawnQueue :+ Enemy(WhaleID, waveNumber) 
          case 13 => spawnQueue = spawnQueue :+ Enemy(MegalodonID, waveNumber) 
        }
      }
    }
    waveNumber += 1
    waves.enqueue(new Wave(waveNumber, 0,0,0,0,0,0,0,   0,0,0,0,0,0,0))
    for (t <- towers) {
      money += t.startRound()
    }
  }
  def spawn() = {
    if (timeToSpawn == 0) {
      val e = spawnQueue.head
      enemies = e :: enemies
      spawnQueue = spawnQueue.tail
      val l = Enemy.getLayer(e.id)
      map(l).spawn(e)
      if (e.id == FishID) {
        timeToSpawn = spawnRate/2
      } else {
        timeToSpawn = spawnRate
      }
    } else {
      timeToSpawn -= 1
    }
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

  def getMap(layer: Layer) = map(layer)

  def toggleSpeed() = {
    numTicks = numTicks % numSpeeds + 1
    println(numTicks)
  }

  var isGameOver = false
  def gameOver() = {
    isGameOver = true
  }

  def upgrade(t: Tower) : Unit = {
    val cost = t.upgradeCost
    if (money >= cost) {
      t.upgrade()
      money -= cost
    }
  }

}
