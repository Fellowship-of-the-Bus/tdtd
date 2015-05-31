package com.github.fellowship_of_the_bus
package tdtd
package game
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import IDMap._
import lib.game.GameConfig.{Height,Width}
import scala.collection.mutable.{LinkedList}
import scala.collection.immutable.Queue


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

  var enemyNumbers = 
    Array(hippoN, alligatorN, turtleN, dolphinN, penguinN, krakenN, hydraN,
      crabN, squidN, fishN, jellyfishN, sharkN, whaleN, megalodonN)
  val enemyIDs = Array (HippoID, AlligatorID, TurtleID, DolphinID,PenguinID,KrakenID,HydraID,
                  CrabID,SquidID,FishID,JellyfishID,SharkID,WhaleID,MegalodonID)
  override def toString () ={
    enemyNumbers.foldLeft("")((x,c) => x + c.toString)
  }
}



class Game {
  val numSpeeds = 2
  private var numTicks = 1

  private var lives = 10
  var money = 100
  private var waveNumber = 0
  var map = Array(new GameMap(), new GameMap())

  private var towers = List[Tower]()
  private var enemies = List[Enemy]()
  var projectiles = List[Projectile]()
  var waves = Queue[Wave]()
  var spawnQueue = LinkedList[Enemy]()
  waves = waves.enqueue(new Wave(1,   0,10,0,0,0,0,0,     0,0,0,0,0,0,0))   //normal/none
  waves = waves.enqueue(new Wave(2,   0,0,0,0,0,0,0,     0,0,0,0,10,0,0))   //none/normal
  waves = waves.enqueue(new Wave(3,   0,15,0,0,0,0,0,     0,0,0,0,15,0,0))  //normal/normal
  waves = waves.enqueue(new Wave(4,   6,0,0,0,0,0,0,     0,0,0,0,0,6,0))    //hp/hp
  waves = waves.enqueue(new Wave(5,   0,0,10,0,0,0,0,     10,0,0,0,0,0,0))  //armour/armour
  waves =  waves.enqueue(new Wave(6,   0,0,0,10,0,0,0,     0,10,0,0,0,0,0))    //fast/fast
  waves =  waves.enqueue(new Wave(7,   0,15,0,0,0,0,0,     0,0,30,5,0,0,0))    //normal/swarm+split
  waves =  waves.enqueue(new Wave(8,   5,0,0,8,0,0,0,     0,0,0,0,12,0,0))    //hp+fast/normal
  waves =  waves.enqueue(new Wave(9,   0,0,0,0,0,0,1,     0,0,0,0,15,0,0))    //boss/normal
  waves =  waves.enqueue(new Wave(10,   0,0,10,0,0,0,0,     0,0,0,0,0,0,1))   //armour/boss
  waves =  waves.enqueue(new Wave(11,   5,0,8,0,0,0,0,     0,0,30,0,0,0,0))   //hp+armour/swarm
  waves =  waves.enqueue(new Wave(12,   0,15,0,10,0,0,0,     0,15,0,0,0,0,0))   //normal+fast/fast
  waves =  waves.enqueue(new Wave(13,   10,0,10,0,0,0,0,     10,0,0,6,0,0,0))   //armour+hp/split+armour
  waves =  waves.enqueue(new Wave(14,   0,0,10,10,0,0,0,     0,0,35,6,0,0,0))   //fast+armour/swarm+split
  waves =  waves.enqueue(new Wave(15,   0,0,0,0,0,0,1,     0,0,35,0,0,0,0))   //boss/swarm
  waves =  waves.enqueue(new Wave(16,   0,0,10,0,0,0,0,     0,0,0,0,0,0,1))   //armour/boss
  waves =  waves.enqueue(new Wave(17,   6,10,0,8,0,0,0,     0,10,30,5,6,0,0))   //all but armour/all but armour
  waves =  waves.enqueue(new Wave(18,   6,10,8,8,0,0,0,     10,10,30,5,6,0,0))   //everything/everything
  waves =  waves.enqueue(new Wave(19,   0,0,0,0,0,0,2,     0,0,0,0,0,0,2))   //boss/boss
  waves =  waves.enqueue(new Wave(20,   4,6,6,6,0,0,2,     8,8,35,4,4,0,2))   //boss+everything/boss+everything
  
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

      if (!spawnQueue.isEmpty) spawn

      for (t <- towers; if (t.active)) {
        val p = t.tick()
        projectiles = p ++ projectiles
      }

      for (e <- enemies; if (e.active)) {
        if (e.tick()) {
          lives -= 1
          if (lives == 0) gameOver
        }
      }

      for (p <- projectiles; if (p.active)) {
        money += p.tick()
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

  def newRoundReady() = enemies.isEmpty && !isGameOver

  def difficulty(i: Int)= {
//    math.exp(i.toFloat*0.05f).toFloat
    if (i <= 20) {
      1f
    } else {
      ((i-20)*(i-20)).toFloat/400f + 1f
    }
  }
  def sendNextWave() = {
    var (w, tmp) = waves.dequeue
    waves = tmp
    for ( i <- 0 to 13) {
      for (j <- 0 to w.enemyNumbers(i)-1) {
        i match {
          case 0 => spawnQueue = spawnQueue :+ Enemy(HippoID, difficulty(waveNumber))
          case 1 => spawnQueue = spawnQueue :+ Enemy(AlligatorID, difficulty(waveNumber)) 
          case 2 => spawnQueue = spawnQueue :+ Enemy(TurtleID, difficulty(waveNumber)) 
          case 3 => spawnQueue = spawnQueue :+ Enemy(DolphinID, difficulty(waveNumber)) 
          case 4 => spawnQueue = spawnQueue :+ Enemy(PenguinID, difficulty(waveNumber)) 
          case 5 => spawnQueue = spawnQueue :+ Enemy(KrakenID, difficulty(waveNumber)) 
          case 6 => spawnQueue = spawnQueue :+ Enemy(HydraID, difficulty(waveNumber)) 
          case 7 => spawnQueue = spawnQueue :+ Enemy(CrabID, difficulty(waveNumber)) 
          case 8 => spawnQueue = spawnQueue :+ Enemy(SquidID, difficulty(waveNumber)) 
          case 9 => spawnQueue = spawnQueue :+ Enemy(FishID, difficulty(waveNumber)) 
          case 10 => spawnQueue = spawnQueue :+ Enemy(JellyfishID, difficulty(waveNumber)) 
          case 11 => spawnQueue = spawnQueue :+ Enemy(SharkID, difficulty(waveNumber)) 
          case 12 => spawnQueue = spawnQueue :+ Enemy(WhaleID, difficulty(waveNumber)) 
          case 13 => spawnQueue = spawnQueue :+ Enemy(MegalodonID, difficulty(waveNumber)) 
        }
      }
    }
    waveNumber += 1
    w.enemyNumbers = w.enemyNumbers.map(x => (x*difficulty(waveNumber/2)).toInt)
    waves = waves.enqueue(w)
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
        timeToSpawn = spawnRate/4
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

  def toggleSpeed(): Int = {
    numTicks = numTicks % numSpeeds + 1
    numTicks
  }

  var isGameOver = false
  def gameOver() = {
    isGameOver = true
  }

  def getLives() = lives
  def getWaveNumber() = waveNumber

  def upgrade(t: Tower) : Unit = {
    val cost = t.upgradeCost
    if (money >= cost) {
      t.upgrade()
      money -= cost
    }
  }

}
