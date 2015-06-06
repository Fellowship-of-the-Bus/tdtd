package com.github.fellowship_of_the_bus
package tdtd
package game

import lib.util.rand
import lib.game.Lifebar
import scala.math._
import IDMap._
import TopLayer._
import BottomLayer._
import GameMap._

object Enemy {
  def apply (eid: Int, mult: Float) = {
    eid match {
      case FishID => new Fish(mult)
      case CrabID => new Crab(mult)
      case SharkID => new Shark(mult)
      case WhaleID => new Whale(mult)
      case JellyfishID => new Jellyfish(mult)
      case SquidID => new Squid(mult)
      case MegalodonID => new Megalodon(mult)

      case AlligatorID => new Alligator(mult)
      case TurtleID => new Turtle(mult)
      case HydraID => new Hydra(mult)
      case HippoID => new Hippo(mult)
      case DolphinID => new Dolphin(mult)
    }
  }
  def getLayer(id: Int) = {
      if (id>= IDMap.UnderStart && id <= IDMap.UnderEnd) {
          BottomLayer
      } else {
          TopLayer
      }
  }
}

trait EnemyType {
  def difficulty: Int
  def id: Int
  def maxHp: Float
  def armor: Float
  def speed: Float
  def width: Float
  def height: Float
}

// tuple containing the magnitude and time remaining on a slow effect 
class SlowEffect(val mult: Float, var time: Int) {}

// triple containing info projectile needs when it hits an enemy
class GotHit(val dmg: Float, val money: Int) {}
// Might need to update for jellyfish duplication

abstract class Enemy (val mult: Float, b: EnemyType) extends GameObject(0,0) with Lifebar { 
	val base = b
	val id = base.id
  var hp = (base.maxHp * mult)
  var armor = (base.armor * mult)
  val width = base.width
  val height = base.height
  var place : Tile = null //(map(0,0).getOrElse(null)) // update when Spawn is known
  var speed = base.speed
  var slows: List[SlowEffect] = List()
  var dir = 0

  val dirCheckTime = 10
  var tileDist = 0f

 	def special() {}

  def maxHp = base.maxHp * mult
  

  def tick() : Boolean = {
  	var maxSlow = 1.0f
    var dist = speed
  	
  	def updateSlow(lst: List[SlowEffect], eff: SlowEffect) = {
  		eff.time -= 1
  		 if (eff.mult < maxSlow) {
  		 	maxSlow = eff.mult
  		 }

  		 if (eff.time > 0) {
  		 	eff :: lst
  		 } else {
  		 	lst
  		 }
  	}

  	special();
  	slows = slows.foldLeft(List[SlowEffect]())((lst, eff) => updateSlow(lst, eff))
    dist = speed * maxSlow
    if (tileDist <= 0) {
      dir = place.direction
    } else {
      tileDist -= dist
    }

    if (dir == Right) {
      rotation = 90
      c = c + dist
    } else if (dir == Left) {
      rotation = 270
      c = c - dist
    } else if (dir == Down) {
      rotation = 180
      r = r + dist
    } else {
      rotation = 0
      r = r - dist
    }

    println(c)
    println(r)

    val nextPlace = map(r,c)
    nextPlace match {
      case Some(tile) =>
        if (place != tile) {
          place.deregister(this)
          place = tile
          place.register(this)
          tileDist = -1f  //+ (0.25f * rand(3))
        }
        false

      case _ =>
        place.deregister(this)
        inactivate
        true
    }
	}

	def hit(dmg: Float) : GotHit = {
		var dmgDone = max(dmg - armor, 0)
    hp -= dmgDone
		if (hp <= 0) {
			place.deregister(this)
			inactivate
      new GotHit(dmgDone, base.difficulty)
		} else {
      new GotHit(dmgDone, 0)
    }
	}

	def slow(eff: SlowEffect) {
		slows = eff :: slows
	}

  override def setMap(m: GameMap) {
      map = m
      place = map(r,c).get
  }

}

object Fish extends EnemyType {
  val id =  FishID
  val difficulty = 1
  val maxHp = 10.0f
  val armor = 0.0f
  val speed = 0.04f
  val width = 0.5f
  val height = 0.5f
}

class Fish(mult: Float) extends Enemy(mult, Fish) {} 

object Hippo extends EnemyType {
  val id = HippoID
  val difficulty = 1
  val maxHp = 30.0f
  val armor = 0.0f
  val speed = 0.03f
  val width = 0.5f
  val height = 0.5f
}

class Hippo(mult: Float) extends Enemy(mult, Hippo) {} 

object Alligator extends EnemyType {
  val id = AlligatorID
  val difficulty = 3
  val maxHp = 18.0f
  val armor = 0.0f
  val speed = 0.04f
  val width = 0.5f
  val height = 0.5f
}

class Alligator(mult: Float) extends Enemy(mult, Alligator) {} 

object Turtle extends EnemyType {
  val id = TurtleID
  val difficulty = 7
  val maxHp = 25.0f
  val armor = 4f
  val speed = 0.035f
  val width = 0.5f
  val height = 0.5f
}

class Turtle(mult: Float) extends Enemy(mult, Turtle) {} 

object Dolphin extends EnemyType {
  val id = DolphinID
  val difficulty = 4
  val maxHp = 30.0f
  val armor = 0.0f
  val speed = 0.05f
  val width = 0.5f
  val height = 0.5f
}

class Dolphin(mult: Float) extends Enemy(mult, Dolphin) {
  var oldDir = -1

  override def special() {
    if (dir == oldDir) {
        speed += 0.0005f
      } else {
        speed = base.speed
      }
  }
} 

object Penguin extends EnemyType {
  val id = PenguinID
  val difficulty = 1
  val maxHp = 10.0f
  val armor = 0.0f
  val speed = 0.05f
  val width = 0.5f
  val height = 0.5f
}

class Penguin(mult: Float) extends Enemy(mult, Penguin) {} 

object Kraken extends EnemyType {
  val id = KrakenID
  val difficulty = 1
  val maxHp = 10.0f
  val armor = 0.0f
  val speed = 0.05f
  val width = 0.5f
  val height = 0.5f
}

class Kraken(mult: Float) extends Enemy(mult, Kraken) {} 

object Hydra extends EnemyType {
  val id = HydraID
  val difficulty = 100
  val maxHp = 300.0f
  val armor = 0.0f
  val speed = 0.03f
  val width = 0.5f
  val height = 0.5f
}

class Hydra(mult: Float) extends Enemy(mult, Hydra) {
  override def special() {
    hp = min(base.maxHp, hp + (base.maxHp * 0.001f))
  }
} 

object Crab extends EnemyType {
  val id = CrabID
  val difficulty = 6
  val maxHp = 25.0f
  val armor = 3.0f
  val speed = 0.035f
  val width = 0.5f
  val height = 0.5f
}

class Crab(mult: Float) extends Enemy(mult, Crab) {} 

object Squid extends EnemyType {
  val id = SquidID
  val difficulty = 2
  val maxHp = 15.0f
  val armor = 0.0f
  val speed = 0.05f
  val width = 0.5f
  val height = 0.5f
}

class Squid(mult: Float) extends Enemy(mult, Squid) {} 

object Jellyfish extends EnemyType {
  val id = JellyfishID
  val difficulty = 5
  val maxHp = 50.0f
  val armor = 0.0f
  val speed = 0.04f
  val width = 0.5f
  val height = 0.5f
}

class Jellyfish(mult: Float) extends Enemy(mult, Jellyfish) {} 

object Whale extends EnemyType {
  val id = WhaleID
  val difficulty = 10
  val maxHp = 100.0f
  val armor = 0.0f
  val speed = 0.03f
  val width = 0.7f
  val height = 0.7f
}

class Whale(mult: Float) extends Enemy(mult, Whale) {} 

object Shark extends EnemyType {
  val id = SharkID
  val difficulty = 3
  val maxHp = 20.0f
  val armor = 0.0f
  val speed = 0.04f
  val width = 0.5f
  val height = 0.5f
}

class Shark(mult: Float) extends Enemy(mult, Shark) {} 

object Megalodon extends EnemyType {
  val id = MegalodonID
  val difficulty = 100
  val maxHp = 400.0f
  val armor = 0.0f
  val speed = 0.035f
  val width = 1.0f
  val height = 1.0f
}

class Megalodon(mult: Float) extends Enemy(mult, Megalodon) {} 
