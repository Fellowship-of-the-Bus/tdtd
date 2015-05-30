package com.github.fellowship_of_the_bus
package tdtd
package game

import IDMap._
import scala.collection.mutable.Set
import lib.game.GameConfig

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
	def value: Int
	def value_=(i: Int): Unit
}

abstract class Tower(xc: Float, yc: Float, towerType: TowerType) extends GameObject(xc, yc) {
	protected var nextShot = 0
	var kind = towerType
	val id = kind.id

	val height = 1.0f
	val width = 1.0f

	def sell(): Int = {
		inactivate
		kind.value / 2
	}

	def upgrade(): Unit

	def upgradeCost(): Int

	def startRound(): Int = 0

	def tick() : Option[Projectile] = {
		if(nextShot == 0) {
			val enemies = map.aoe(r, c, kind.range)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				val target = kind.currAI.pick(r, c, enemies)
				val proj = Projectile(r, c, target, this)
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

	def setAI(ai: AI) : Unit = {
		kind.currAI = ai
	}
}

class HarpoonTower(xc: Float, yc: Float) extends Tower(xc, yc, HarpoonTower) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {}
}

object HarpoonTower extends TowerType {
	var range = 4.0f
	var damage = 1.0f
	var fireRate = GameConfig.FrameRate
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = HarpoonTowerID
	var speed = 2.0f
	var value = 5
}

class CannonTower(xc: Float, yc: Float) extends Tower(xc, yc, CannonTower) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {}
}

object CannonTower extends TowerType {
	var range = 4.0f
	var damage = 5.0f
	var fireRate = 20
	var aoe = 2.0f
	var currAI: AI = new RandomAI
	var id = CannonTowerID
	var speed = 1.0f
	var value = 20
}

class TorpedoTower(xc: Float, yc: Float) extends Tower(xc, yc, TorpedoTower) {
	private var maps = List[GameMap]()
	override def setMap(m: GameMap): Unit = {
		maps = m :: maps
	}

	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {}

	override def tick() : Option[Projectile] = {
		if(nextShot == 0) {
			val enemies = maps.foldRight(Set[Enemy]())((map, set) =>
				if (set.isEmpty) {
					val enemies = map.aoe(r, c, kind.range)
					if (!enemies.isEmpty) {
						enemies
					} else {
						set
					}
				} else {
					set
				}
			)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				val target = kind.currAI.pick(r, c, enemies)
				val proj = Projectile(r, c, target, this)
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

object TorpedoTower extends TowerType {
	var range = 4.0f
	var damage = 5.0f
	var fireRate = 20
	var aoe = 2.0f
	var currAI: AI = new RandomAI
	var id = TorpedoTowerID
	var speed = 1.0f
	var value = 25
}

class OilDrillTower(xc: Float, yc: Float) extends Tower(xc, yc, OilDrillTower) {
	private var maps = List[GameMap]()
	override def setMap(m: GameMap): Unit = {
		maps = m :: maps
	}

	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {}

	override def tick() : Option[Projectile] = None

	override def startRound() : Int = {
		kind.value / 10
	}
}

object OilDrillTower extends TowerType {
	var range = 0.0f
	var damage = 0.0f
	var fireRate = 0
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = OilDrillTowerID
	var speed = 0.0f
	var value = 50
}

class IceTowerBottom(xc: Float, yc: Float) extends Tower(xc, yc, IceTowerBottom) {
	var slowMult = 0.75f
	var slowTime = 30
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {
		slowMult -= 0.1f
	}

	override def tick() : Option[Projectile] = {
		if (nextShot == 0) {
			val enemies = map.aoe(r,c, kind.range)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				enemies.foreach(enemy => {
						val slow = new SlowEffect(slowMult, slowTime)
						enemy.slow(slow)
					}
				)
			}
			None
		} else {
			nextShot -= 1
			None
		}
	}
}

object IceTowerBottom extends TowerType {
	var range = 4.0f
	var damage = 0.0f
	var fireRate = 10
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = IceTowerBottomID
	var speed = 2.0f
	var value = 5
}

class IceTowerTop(xc: Float, yc: Float) extends Tower(xc, yc, IceTowerTop) {
	def upgradeCost(): Int = 0

	def upgrade(): Unit = {}

	override def tick() : Option[Projectile] = None
}

object IceTowerTop extends TowerType {
	var range = 0.0f
	var damage = 0.0f
	var fireRate = 120
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = IceTowerBottomID
	var speed = 0.0f
	var value = 0
}

class DepthChargeTower(xc: Float, yc: Float) extends Tower(xc, yc, DepthChargeTower) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {}
}

object DepthChargeTower extends TowerType {
	var range = 4.0f
	var damage = 5.0f
	var fireRate = 120
	var aoe = 2.0f
	var currAI: AI = new RandomAI
	var id = DepthChargeTowerID
	var speed = 1.0f
	var value = 20
}

object Tower {
	def apply(id: Int, xc: Float, yc: Float) : Tower = {
		id match {
			case HarpoonTowerID => new HarpoonTower(xc, yc)
			case CannonTowerID => new CannonTower(xc, yc)
			case TorpedoTowerID => new TorpedoTower(xc, yc)
			case OilDrillTowerID => new OilDrillTower(xc,yc)
			case IceTowerBottomID => new IceTowerBottom(xc, yc)
			case IceTowerTopID => new IceTowerTop(xc, yc)
			case DepthChargeTowerID => new DepthChargeTower(xc, yc)
		}
	}
}