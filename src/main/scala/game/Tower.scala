package com.github.fellowship_of_the_bus
package tdtd
package game

import IDMap._
import scala.collection.mutable.Set
import lib.game.GameConfig
import scala.math._

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
	def name: String
	def name_=(s: String): Unit
	def describe() : List[String] = {
		val fireSpeed = fireRate / GameConfig.FrameRate.toFloat
		var ret = List(
			s"Value: ${value}",
			f"Damage: ${damage}%.1f",
			f"Fire Rate: $fireSpeed%.1f seconds",
			f"Range: ${range}%.1f"
		)
		if (aoe != 0.0f) {
			ret = ret ++ List(f"Area of Effect: ${aoe}%.1f")
		}
		ret = ret ++ List(
			s"Default AI: ${currAI}"
		)
		ret
	}
}

abstract class Tower(xc: Float, yc: Float, towerType: TowerType) extends GameObject(xc, yc) {
	protected var nextShot = 0
	val kind = towerType
	val id = towerType.id

	val height = 1.0f
	val width = 1.0f
	// r += 0.5f
	// c += 0.5f
	var kills = 0
	var dmgDone = 0
	var level = 1

	def sell(): Int = {
		inactivate
		kind.value / 2
	}

	def upgrade(): Unit

	def upgradeCost(): Int

	def startRound(): Int = 0

	def setRotation(tar: Enemy) {
		val rVec = tar.r - r
    	var cVec = tar.c - c
    	val theta = atan2(rVec, cVec)
      	rotation = toDegrees(theta).asInstanceOf[Float] + 90f
	}

	def tick() : List[Projectile] = {
		if(nextShot <= 0) {
			val enemies = map.aoe(r, c, kind.range)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				val target = kind.currAI.pick(r, c, enemies)
				setRotation(target)
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

	def describe() : List[String] = {
		val fireSpeed = kind.fireRate / GameConfig.FrameRate.toFloat
		var ret = List(
			s"Value: ${kind.value}",
			f"Damage: ${kind.damage}%.1f",
			f"Fire Rate: $fireSpeed%.1f seconds",
			f"Range: ${kind.range}%.1f"
		)
		if (kind.aoe != 0.0f) {
			ret = ret ++ List(f"Area of Effect: ${kind.aoe}%.1f")
		}
		ret = ret ++ List(
			s"Current AI: ${kind.currAI}",
			s"Kills: $kills", 
			f"Damage Dealt: $dmgDone%.1f"
		)
		ret
	}
}

abstract class SlowingTower(xc: Float, yc: Float, towerType: SlowingTowerType) extends Tower(xc, yc, towerType) {
	override val kind = towerType

	override def tick() : List[Projectile] = {
		if (nextShot == 0) {
			val enemies = map.aoe(r,c, kind.range)
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				enemies.foreach(enemy => {
						val slow = new SlowEffect(towerType.slowMult, towerType.slowTime)
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

	

	override def describe() : List[String] = {
		val time = towerType.slowTime / GameConfig.FrameRate.toFloat
		val mult = (towerType.slowMult * 100).toInt
		var ret = List(
			s"Value: ${towerType.value}",
			s"Slow Multiplier: $mult%",
			f"Slow Time: $time%.1f seconds",
			f"Range: ${towerType.range}%.1f"
		)
		ret
	}
}

abstract class MazingTower(xc: Float, yc: Float, towerType: TowerType) extends Tower(xc, yc, towerType) {
	override def tick() : List[Projectile] = List()
}

trait SlowingTowerType extends TowerType {
	def slowMult: Float
	def slowMult_=(m: Float): Unit
	def slowTime : Int
	def slowTime_=(t: Int): Unit

	override def describe() : List[String] = {
		val time = slowTime / GameConfig.FrameRate.toFloat
		val mult = (slowMult * 100).toInt
		var ret = List(
			s"Value: ${value}",
			s"Slow Multiplier: $mult%",
			f"Slow Time: $time%.1f seconds",
			f"Range: ${range}%.1f"
		)
		ret
	}
}

class HarpoonTower(xc: Float, yc: Float) extends Tower(xc, yc, HarpoonTower) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = { kind.damage += 1
		kind.fireRate -= 1}
}

object HarpoonTower extends TowerType {
	var range = 30.0f
	var damage = 3.0f
	var fireRate = 30
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = HarpoonTowerID
	var projectileID = HarpoonID
	var speed = 0.1f
	var value = 5
	var name = "Harpoon Tower"
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
	var fireRate = 120
	var aoe = 4.0f
	var currAI: AI = new ClosestToGoalAI
	var id = CannonTowerID
	var projectileID = HarpoonID
	var speed = 0.1f
	var value = 20
	var name = "Cannon Tower"
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
				setRotation(target)
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
	var name = "Torpedo Tower"
}

class OilDrillTower(xc: Float, yc: Float) extends MazingTower(xc, yc, OilDrillTower) {
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

	override def describe() : List[String] = {
		var cash = kind.value / 10
		var ret = List(
			s"Value: ${kind.value}",
			s"Cash Earned per Round: $cash"
		)
		ret
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
	var name = "Oil Drill"

	override def describe() : List[String] = {
		var cash = value / 10
		var ret = List(
			s"Value: ${value}",
			s"Cash Earned per Round: $cash"
		)
		ret
	}
}

class IceTowerBottom(xc: Float, yc: Float) extends SlowingTower(xc, yc, IceTowerBottom) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {
		kind.slowMult -= 0.1f
	}

}

object IceTowerBottom extends SlowingTowerType {
	var range = 4.0f
	var damage = 0.0f
	var fireRate = 10
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = IceTowerBottomID
	var projectileID = HarpoonID
	var speed = 2.0f
	var value = 15
	var name = "Ice Tower"
	var slowMult = 0.75f
	var slowTime = 20
}

class IceTowerTop(xc: Float, yc: Float) extends MazingTower(xc, yc, IceTowerTop) {
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
	var name = "Ice Tower"
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
	var name = "Depth Charge"
}

class WhirlpoolBottom(xc: Float, yc: Float) extends MazingTower(xc, yc, WhirlpoolBottom) {
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
	var name = "Whirlpool"
}

class WhirlpoolTop(xc: Float, yc: Float) extends SlowingTower(xc, yc, WhirlpoolTop) {
	def upgradeCost(): Int = {
		1
	}

	def upgrade(): Unit = {
		kind.slowMult -= 0.1f
	}
}

object WhirlpoolTop extends SlowingTowerType {
	var range = 0.0f
	var damage = 0.0f
	var fireRate = 120
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = WhirlpoolTopID
	var projectileID = HarpoonID
	var speed = 0.0f
	var value = 0
	var name = "Whirlpool"
	var slowMult = 0.75f
	var slowTime = 20
}

class MissileTower(xc: Float, yc: Float) extends Tower(xc, yc, MissileTower) {
	var numTargets = 3
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
					if (!enemies.isEmpty) {
						val target = kind.currAI.pick(r, c, enemies)
						if (i == 1) {
							setRotation(target)
						}
						val proj = Projectile(r, c, target, this)
						proj.setMap(map)
						projectiles = proj :: projectiles
						enemies.remove(target)
					}
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
	var projectileID = MissileID
	var speed = 0.2f
	var value = 40
	var name = "Missile Tower"
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
				setRotation(target)
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
	var name = "Net Tower"
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

			for(i <- 1 to kind.range.toInt) {
				map(r+1,c) match {
					case Some(tile) => enemiesU ++= tile.enemies
					case None => ()
				}
				map(r-1,c) match {
					case Some(tile) => enemiesD ++= tile.enemies
					case None => ()
				}
				map(r,c+1) match {
					case Some(tile) => enemiesR ++= tile.enemies
					case None => ()
				}
				map(r,c-1) match {
					case Some(tile) => enemiesL ++= tile.enemies
					case None => ()
				}
			}
			val enemies = enemiesU ++ enemiesL ++ enemiesR ++ enemiesD
			if (!enemies.isEmpty) {
				nextShot = kind.fireRate
				val target = kind.currAI.pick(r, c, enemiesU, enemiesD, enemiesL, enemiesR)
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
	var currAI: AI = new SteamRandomAI
	var id = SteamTowerID
	var projectileID = HarpoonID
	var speed = 1.0f
	var value = 20
	var name = "Steam Tower"
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
	def apply(id: Int) : TowerType = {
		id match {
			case HarpoonTowerID => HarpoonTower
			case CannonTowerID => CannonTower
			case TorpedoTowerID => TorpedoTower
			case OilDrillTowerID => OilDrillTower
			case IceTowerBottomID => IceTowerBottom
			case IceTowerTopID => IceTowerTop
			case DepthChargeTowerID => DepthChargeTower
			case WhirlpoolBottomID => WhirlpoolBottom
			case WhirlpoolTopID => WhirlpoolTop
			case MissileTowerID => MissileTower
			case NetTowerID => NetTower
			case SteamTowerID => SteamTower
		}
	}
}