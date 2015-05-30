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
	def projectileID: Int
	def projectileID_=(p: Int): Unit
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

	def tick() : List[Projectile] = {
		if(nextShot == 0) {
			val enemies = map.aoe(r, c, kind.range)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				val target = kind.currAI.pick(r, c, enemies)
				val proj = Projectile(r, c, target, this)
				proj.setMap(map)
				List(proj)
			} else {
				List()
			}
		} else {
			nextShot -= 1
			List()
		}
	}

	def setAI(ai: AI) : Unit = {
		kind.currAI = ai
	}
}

abstract class SlowingTower(xc: Float, yc: Float, towerType: TowerType) extends Tower(xc, yc, towerType) {
	var slowMult = 0.75f
	var slowTime = 30

	override def tick() : List[Projectile] = {
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
			List()
		} else {
			nextShot -= 1
			List()
		}
	}
}

abstract class MazingTower(xc: Float, yc: Float, towerType: TowerType) extends Tower(xc, yc, towerType) {
	override def tick() : List[Projectile] = List()
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
	var projectileID = HarpoonID
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
	var projectileID = HarpoonID
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

	override def tick() : List[Projectile] = {
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
				List(proj)
			} else {
				List()
			}
		} else {
			nextShot -= 1
			List()
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
	var projectileID = HarpoonID
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
	var projectileID = HarpoonID
	var speed = 0.0f
	var value = 50
}

class IceTowerBottom(xc: Float, yc: Float) extends SlowingTower(xc, yc, IceTowerBottom) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {
		slowMult -= 0.1f
	}

}

object IceTowerBottom extends TowerType {
	var range = 4.0f
	var damage = 0.0f
	var fireRate = 10
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = IceTowerBottomID
	var projectileID = HarpoonID
	var speed = 2.0f
	var value = 5
}

class IceTowerTop(xc: Float, yc: Float) extends Tower(xc, yc, IceTowerTop) {
	def upgradeCost(): Int = 0

	def upgrade(): Unit = {}

}

object IceTowerTop extends TowerType {
	var range = 0.0f
	var damage = 0.0f
	var fireRate = 120
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = IceTowerBottomID
	var projectileID = HarpoonID
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
	var projectileID = HarpoonID
	var speed = 1.0f
	var value = 20
}

class WhirlpoolBottom(xc: Float, yc: Float) extends Tower(xc, yc, WhirlpoolBottom) {
	var slowMult = 0.75f
	var slowTime = 30
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {}
}

object WhirlpoolBottom extends TowerType {
	var range = 4.0f
	var damage = 0.0f
	var fireRate = 10
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = WhirlpoolBottomID
	var projectileID = HarpoonID
	var speed = 2.0f
	var value = 5
}

class WhirlpoolTop(xc: Float, yc: Float) extends SlowingTower(xc, yc, WhirlpoolTop) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {
		slowMult -= 0.1f
	}
}

object WhirlpoolTop extends TowerType {
	var range = 0.0f
	var damage = 0.0f
	var fireRate = 120
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = WhirlpoolTopID
	var projectileID = HarpoonID
	var speed = 0.0f
	var value = 0
}

class MissileTower(xc: Float, yc: Float) extends Tower(xc, yc, MissileTower) {
	var numTargets = 2
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {
		numTargets += 1

	}

	override def tick() : List[Projectile] = {
		if(nextShot == 0) {
			val enemies = map.aoe(r, c, kind.range)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				var projectiles = List[Projectile]()
				for(i <- 1 to numTargets) {
					val target = kind.currAI.pick(r, c, enemies)
					val proj = Projectile(r, c, target, this)
					proj.setMap(map)
					projectiles = proj :: projectiles
					enemies.remove(target)
				}
				projectiles
			} else {
				List()
			}
		} else {
			nextShot -= 1
			List()
		}
	}
}

object MissileTower extends TowerType {
	var range = 5.0f
	var damage = 10.0f
	var fireRate = 90
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = MissileTowerID
	var projectileID = HarpoonID
	var speed = 1.0f
	var value = 40
}

class NetTower(xc: Float, yc: Float) extends Tower(xc, yc, NetTower) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {}

	override def tick(): List[Projectile] = {
		if(nextShot == 0) {
			val enemies = map.aoe(r, c, kind.range)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				val target = kind.currAI.pick(r, c, enemies)
				val proj = Projectile(r, c, target, this)
				proj.setMap(map)
				List(proj)
			} else {
				List()
			}
		} else {
			nextShot -= 1
			List()
		}
	}
}

object NetTower extends TowerType {
	var range = 4.0f
	var damage = 5.0f
	var fireRate = 120
	var aoe = 2.0f
	var currAI: AI = new RandomAI
	var id = NetTowerID
	var projectileID = HarpoonID
	var speed = 1.0f
	var value = 20
}

class SteamTower(xc: Float, yc: Float) extends Tower(xc, yc, SteamTower) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {}

	override def tick(): List[Projectile] = {
		if(nextShot == 0) {
			var enemiesU = Set[Enemy]()
			var enemiesL = Set[Enemy]()
			var enemiesD = Set[Enemy]()
			var enemiesR = Set[Enemy]()

			val enemies = enemiesU ++ enemiesL ++ enemiesR ++ enemiesD
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				val target = kind.currAI.pick(r, c, enemies)
				val proj = Projectile(r, c, target, this)
				proj.setMap(map)
				List(proj)
			} else {
				List()
			}
		} else {
			nextShot -= 1
			List()
		}
	}
}

object SteamTower extends TowerType {
	var range = 4.0f
	var damage = 5.0f
	var fireRate = 120
	var aoe = 2.0f
	var currAI: AI = new RandomAI
	var id = SteamTowerID
	var projectileID = HarpoonID
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
			case WhirlpoolBottomID => new WhirlpoolBottom(xc, yc)
			case WhirlpoolTopID => new WhirlpoolTop(xc, yc)
			case MissileTowerID => new MissileTower(xc, yc)
			case NetTowerID => new NetTower(xc, yc)
			case SteamTowerID => new SteamTower(xc, yc)
		}
	}
}