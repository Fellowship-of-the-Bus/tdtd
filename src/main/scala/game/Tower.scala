package com.github.fellowship_of_the_bus
package tdtd
package game
import IDMap._

trait TowerType {
	def damage: Float
	def damage_=(d: Float): Unit
	def fireRate: Int
	def fireRate_=(r: Int): Unit
	def range: Float
	def range_=(r: Float): Unit
	def aoe: Float
	def aoe_=(area: Float): Unit
	def currAI: AI
	def currAI_=(ai: AI) : Unit
	def id: Int
	def id_=(i: Int): Unit
	def speed: Float
	def speed_=(s: Float): Unit
}

abstract class Tower(xc: Float, yc: Float, towerType: TowerType, cost: Int) extends GameObject(xc, yc) {
	private var value = cost
	private var nextShot = 0
	var kind = towerType
	var id = 0

	val height = 1.0f
	val width = 1.0f

	def sell(): Int = {
		inactivate
		value / 2
	}

	def upgrade(): Unit = {

	}

	def startRound(): Unit = {}

	def tick() : Option[Projectile] = {
		if(nextShot == 0) {
			val enemies = map.aoe(r, c, kind.range)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				val target = kind.currAI.pick(r, c, enemies)
				var proj = Projectile(r, c, target, this)
				proj.setMap(map)
				Some(proj)
			} else {
				None
			}
		} else {
			nextShot -= 1
			None
		}
	}
}


class HarpoonTower(xc: Float, yc: Float) extends Tower(xc, yc, HarpoonTower, 5) {
}

object HarpoonTower extends TowerType {
	var range = 4.0f
	var damage = 1.0f
	var fireRate = 20
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = HarpoonTowerID
	var speed = 2.0f
}

class CannonTower(xc: Float, yc: Float) extends Tower(xc, yc, CannonTower, 10) {
}

object CannonTower extends TowerType {
	var range = 4.0f
	var damage = 5.0f
	var fireRate = 20
	var aoe = 2.0f
	var currAI: AI = new RandomAI
	var id = CannonTowerID
	var speed = 1.0f
}

class TorpedoTower(xc: Float, yc: Float) extends Tower(xc, yc, CannonTower, 10) {
	private var maps = List[GameMap]()
	override def setMap(m: GameMap): Unit = {
		maps = m :: maps
	}

	override def tick() : Option[Projectile] = {None}
}

object TorpedoTower extends TowerType {
	var range = 4.0f
	var damage = 5.0f
	var fireRate = 20
	var aoe = 2.0f
	var currAI: AI = new RandomAI
	var id = CannonTowerID
	var speed = 1.0f
}

object Tower {
	def apply(id: Int, xc: Float, yc: Float) : Tower = {
		id match {
			case HarpoonTowerID => new HarpoonTower(xc, yc)
			case CannonTowerID => new CannonTower(xc, yc)
			case TorpedoTowerID => new TorpedoTower(xc, yc)
		}
	}
}