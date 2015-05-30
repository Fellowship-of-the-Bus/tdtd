package com.github.fellowship_of_the_bus
package tdtd
package game

import lib.util.rand
import lib.game.Lifebar
import scala.math._



object Enemy {
  def apply (eid: Int, mult: Float) = {
    eid match {
      case 9001 => new Fish(mult) // stub
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

abstract class Enemy (mult: Float, b: EnemyType) extends GameObject(0,0) {//} with Lifebar { 
	val base = b
	val id = base.id
  var hp = (base.maxHp * mult)
  var armor = (base.armor * mult)
  val width = base.width
  val height = base.height
  var place = (map(0,0).getOrElse(null)) // update when Spawn is known
  var speed = base.speed
  var slows: List[SlowEffect] = List()

 	def special() {}


  def tick() : Boolean = {
  	var maxSlow = 0.0f
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
    val dir = place.direction

    if (dir == 0) {
      c = c + dist
    } else if (dir == 1) {
      c = c - dist
    } else if (dir == 2) {
      r = r - dist
    } else {
      r = r + dist
    }

    val nextPlace = map(r,c)
    nextPlace match {
      case Some(tile) =>
        if (place != tile) {
          place.deregister(this)
          place = tile
          place.register(this)
        }
        false

      case _ =>
        place.deregister(this)
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

}

object Fish extends EnemyType {
  val id =  9001//FishID
  val difficulty = 1
  val maxHp = 10.0f
  val armor = 0.0f
  val speed = 0.8f
  val width = 0.5f
  val height = 0.5f
}

class Fish(mult: Float) extends Enemy(mult, Fish) {} 

// object Hippo extends EnemyType {
//   val id = HippoID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Hippo(mult: Float) extends Enemy(mult, Hippo) {} 

// object Alligator extends EnemyType {
//   val id = AlligatorID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Alligator(mult: Float) extends Enemy(mult, Alligator) {} 

// object Turtle extends EnemyType {
//   val id = TurtleID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Turtle(mult: Float) extends Enemy(mult, Turtle) {} 

// object Dolphin extends EnemyType {
//   val id = DolphinID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Dolphin(mult: Float) extends Enemy(mult, Dolphin) {} 

// object Penguin extends EnemyType {
//   val id = PenguinID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Penguin(mult: Float) extends Enemy(mult, Penguin) {} 

// object Kraken extends EnemyType {
//   val id = KrakenID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Kraken(mult: Float) extends Enemy(mult, Kraken) {} 

// object Hydra extends EnemyType {
//   val id = HydraID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Hydra(mult: Float) extends Enemy(mult, Hydra) {} 

// object Crab extends EnemyType {
//   val id = CrabID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Crab(mult: Float) extends Enemy(mult, Crab) {} 

// object Squid extends EnemyType {
//   val id = SquidID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Squid(mult: Float) extends Enemy(mult, Squid) {} 

// object Jellyfish extends EnemyType {
//   val id = JellyfishID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Jellyfish(mult: Float) extends Enemy(mult, Jellyfish) {} 

// object Whale extends EnemyType {
//   val id = WhaleID
//   val difficulty = 5
//   val maxHp = 100.0
//   val armor = 0.0
//   val speed = 0.6
// }

// class Whale(mult: Float) extends Enemy(mult, Whale) {} 