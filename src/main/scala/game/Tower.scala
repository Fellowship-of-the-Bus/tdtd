package com.github.fellowship_of_the_bus
package tdtd
package game

import IDMap._
import scala.collection.mutable.Set
import lib.game.GameConfig
import scala.math._
import GameMap._

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
  def basename: String
	def name_=(s: String): Unit
  def cost : Int
  def cost_=(i:Int): Unit
	def description: String
	def description_=(s: String): Unit
	def hotkey: Char
	def hotkey_=(c: Char): Unit
	def describe() : List[String] = {
		val fireSpeed = fireRate / GameConfig.FrameRate.toFloat
		var ret = List(
			s"Value: ${value}",
      if (cost == 0) {
        "Max Level"
      } else {
        s"Upgrade Cost: $cost"
      },
			f"Damage: ${damage}%.1f",
			f"Fire Rate: $fireSpeed%.1f seconds",
			f"Range: ${range}%.1f"
		)
		if (aoe != 0.0f) {
			ret = ret ++ List(f"Area of Effect: ${aoe}%.1f")
		}
		ret = ret ++ List(
			s"Default AI: ${currAI}",
			s"Description: ${description}",
			s"Hotkey: ${hotkey}"
		)
		ret
	}
	def init() = {
    var att = TowerMap.towerMap((id,1))
    var att1 = TowerMap.towerMap((id,2))
    damage = att.dmg.toFloat
    fireRate = att.rate
    range = att.range
    aoe = att.aoe
    speed = att.projspd
    value = att.cost
    cost = att1.cost
  }
}

abstract class Tower(xc: Float, yc: Float, towerType: TowerType) extends GameObject(xc, yc) {
	protected var nextShot = 0
	val kind = towerType
	val id = towerType.id
	var currAI = towerType.currAI
	val height = 1.0f
	val width = 1.0f
	// r += 0.5f
	// c += 0.5f
	var kills = 0
	var dmgDone = 0f
	var level = 1
	var boughtAI = false

	def sell(): Int = {
		inactivate
		kind.value / 2
	}

  towerType.name = towerType.basename + " Level 1"

  def upgradable () = level < 3
  
  var damage = towerType.damage
  var fireRate = towerType.fireRate
  var range = towerType.range
  var aoe = towerType.aoe
  var speed = towerType.speed
  var value = towerType.value
  var name = towerType.name
  var basename = towerType.basename
  var cost = towerType.cost

	def upgrade() = {
    var att = TowerMap.towerMap((id,level+1))
    if (level != 2) {
      var att1 = TowerMap.towerMap((id,level+2))
      cost = att1.cost
    } else {
      cost = 0
    }
    level += 1
    damage = att.dmg.toFloat
    fireRate = att.rate
    range = att.range
    aoe = att.aoe
    speed = att.projspd
    value += att.cost
    name = basename + s" Level $level"
  }

    

	def upgradeCost() : Int = {
    if (level >=3 ) {
      return 0
    }
    var att = TowerMap.towerMap((id,level+1))
    att.cost
  }

	def startRound(): Int = 0

	def setRotation(tar: Enemy) {
		val rVec = tar.r - r
    	var cVec = tar.c - c
    	val theta = atan2(rVec, cVec)
      	rotation = toDegrees(theta).asInstanceOf[Float] + 90f
	}

	def tick() : List[Projectile] = {
		if(nextShot <= 0) {
			val enemies = map.aoe(r, c, range)
			if (!enemies.isEmpty) {
				nextShot = fireRate
				val target = currAI.pick(r, c, enemies)
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
		currAI = ai
	}

	def describe() : List[String] = {
		val fireSpeed = fireRate / GameConfig.FrameRate.toFloat
		var ret = List(
			s"Value: ${value}",
      if (cost == 0) {
        "Max Level"
      } else {
        s"Upgrade Cost: $cost"
      },
			f"Damage: ${damage}%.1f",
			f"Fire Rate: $fireSpeed%.1f seconds",
			f"Range: ${range}%.1f"
		)
		if (aoe != 0.0f) {
			ret = ret ++ List(f"Area of Effect: ${aoe}%.1f")
		}
		ret = ret ++ List(
			s"Current AI: ${currAI}",
			s"Kills: $kills", 
			f"Damage Dealt: $dmgDone%.1f",
			s"Description: ${kind.description}"
		)
		ret
	}
}

abstract class SlowingTower(xc: Float, yc: Float, towerType: SlowingTowerType) extends Tower(xc, yc, towerType) {
	override val kind = towerType
  var slowMult = towerType.slowMult

  override def upgrade = {
    super.upgrade()
    var att = TowerMap.towerMap((id,level))
    println(s"${att.slow}")
    slowMult = 1f - att.slow
  }
    

          
	override def tick() : List[Projectile] = {
		if (nextShot == 0) {
			val enemies = map.aoe(r,c, range)
			if (!enemies.isEmpty) {
				nextShot = fireRate
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
		val mult = ((1f - slowMult) * 100).toInt
		var ret = List(
			s"Value: ${value}",
      if (cost == 0) {
        "Max Level"
      } else {
        s"Upgrade Cost: $cost"
      },
			s"Slow Multiplier: $mult%",
			f"Slow Time: ${time}%.1f seconds",
			f"Range: ${range}%.1f",
			s"Description: ${towerType.description}"
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
		val mult = ((1f - slowMult) * 100).toInt
		var ret = List(
			s"Value: ${value}",
      if (cost ==0) {
        "Max Level"
      } else {
        s"Upgrade Cost: $cost"
      },
			s"Slow Multiplier: $mult%",
			f"Slow Time: $time%.1f seconds",
			f"Range: ${range}%.1f",
			s"Description: ${description}",
			s"Hotkey: ${hotkey}"
		)
		ret
	}

	override def init() : Unit = {
		super.init()
		var att = TowerMap.towerMap((id,1))
		slowMult = 1f - att.slow 
	}
}

class HarpoonTower(xc: Float, yc: Float) extends Tower(xc, yc, HarpoonTower) {

}

object HarpoonTower extends TowerType {
  var cost = 0
	var range = 2.0f
	var damage = 2.0f
	var fireRate = 60
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = HarpoonTowerID
	var projectileID = HarpoonID
	var speed = 0.25f
	var value = 10
	var basename = "Harpoon Tower"
	var name = "Harpoon Tower Level 1"
	var description = "Basic single\n  target tower, can be placed\n  above water and below water"
	var hotkey = 'H'
  init
}

class CannonTower(xc: Float, yc: Float) extends Tower(xc, yc, CannonTower) {
}

object CannonTower extends TowerType {
  var cost = 0
	var range = 2.5f
	var damage = 5.0f
	var fireRate = 120
	var aoe = 1.0f
	var currAI: AI = new ClosestToGoalAI
	var id = CannonTowerID
	var projectileID = CannonballID
	var speed = 0.2f
	var value = 20
	var basename = "Cannon Tower"
	var name = "Cannon Tower Level 1"
	var description = "Basic AoE tower\n  can only be placed above water"
	var hotkey = 'C'
  init
}

class TorpedoTower(xc: Float, yc: Float) extends Tower(xc, yc, TorpedoTower) {
	private var maps = List[GameMap]()
	override def setMap(m: GameMap): Unit = {
		maps = m :: maps
	}



	override def tick() : List[Projectile] = {
		if(nextShot == 0) {
			val enemies = maps.foldRight(Set[Enemy]())((map, set) => {
					if (set.isEmpty) {
						val enemies = map.aoe(r, c, range)
						if (!enemies.isEmpty) {
							enemies
						} else {
							set
						}
					} else {
						set
					}
				}
			)
			if (!enemies.isEmpty) {
				nextShot = fireRate
				val target = currAI.pick(r, c, enemies)
				setRotation(target)
				val proj = Projectile(r, c, target, this)
				proj.setMap(target.getMap)
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
  var cost = 0
	var range = 3.25f
	var damage = 6.0f
	var fireRate = 90
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = TorpedoTowerID
	var projectileID = TorpedoID
	var speed = 0.3f
	var value = 30
	var basename = "Torpedo Tower"
	var name = "Torpedo Tower Level 1"
	var description = "Single target tower\n  Placed below water, but\n  can fire at both levels"
	var hotkey = 'T'
  init
}

class OilDrillTower(xc: Float, yc: Float) extends MazingTower(xc, yc, OilDrillTower) {
	private var maps = List[GameMap]()
	override def setMap(m: GameMap): Unit = {
		maps = m :: maps
	}


  var cash = 30
	override def upgrade() = {
    var att = TowerMap.towerMap((id,level+1))
    super.upgrade()
    cash = att.money
  }

	override def startRound() : Int = {
		cash
	}

	override def describe() : List[String] = {
		var ret = List(
			s"Value: ${value}",
      if (cost == 0) {
        "Max Level"
      } else {
        s"Upgrade Cost: $cost"
      },
			s"Cash Earned per Round: $cash",
			s"Description: ${kind.description}"
		)
		ret
	}
}

object OilDrillTower extends TowerType {
  var cost = 0
	var range = 0.0f
	var damage = 0.0f
	var fireRate = 0
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = OilDrillTowerID
	var projectileID = HarpoonID
	var speed = 0.0f
	var value = 200
	var basename = "Oil Drill"
	var name = "Oil Drill Level 1"
	var description = "Money generator\n  Earns money at the start\n  of each round\n  Takes up spot above and\n  below water"
	var hotkey = 'O'


	override def describe() : List[String] = {
    val cash = 30
		var ret = List(
			s"Value: ${value}",
      s"Upgrade Cost: $cash",
			s"Cash Earned per Round: $cash",
			s"Description: ${description}"
		)
		ret
	}
  init
}

class IceTowerBottom(xc: Float, yc: Float) extends SlowingTower(xc, yc, IceTowerBottom) {

}

object IceTowerBottom extends SlowingTowerType {
  var cost = 0
	var range = 1.0f
	var damage = 0.0f
	var fireRate = 10
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = IceTowerBottomID
	var projectileID = HarpoonID
	var speed = 2.0f
	var value = 20
	var basename = "Ice Tower"
	var name = "Ice Tower Level 1"
	var slowMult = 0.75f
	var slowTime = 20
	var description = "Slowing tower\n  Placed below and slows in area\n  Adds ice block to same spot\n  above water which blocks\n  enemies"
	var hotkey = 'I'
  init
}

class IceTowerTop(xc: Float, yc: Float) extends MazingTower(xc, yc, IceTowerTop) {
	override def upgradeCost(): Int = 0

	override def upgrade(): Unit = {}

}

object IceTowerTop extends TowerType {
  var cost = 0
	var range = 0.0f
	var damage = 0.0f
	var fireRate = 120
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = IceTowerTopID
	var projectileID = HarpoonID
	var speed = 0.0f
	var value = 0
	var basename = "Ice Tower"
	var name = "Ice Tower Level 1"
	var description = ""
	var hotkey = 'I'
}

class DepthChargeTower(xc: Float, yc: Float) extends Tower(xc, yc, DepthChargeTower) {
}

object DepthChargeTower extends TowerType {
  var cost = 0
	var range = 1.5f
	var damage = 5.0f
	var fireRate = 120
	var aoe = 1.0f
	var currAI: AI = new RandomAI
	var id = DepthChargeTowerID
	var projectileID = HarpoonID
	var speed = 0.2f
	var value = 20
	var basename = "Depth Charge"
	var name = "Depth Charge Level 1"
	var description = "AoE tower\n  Placed above water, but fires\n  at enemies below water"
	var hotkey = 'D'
  init
}

class WhirlpoolBottom(xc: Float, yc: Float) extends MazingTower(xc, yc, WhirlpoolBottom) {
	var slowMult = 0.75f
	var slowTime = 30
}

object WhirlpoolBottom extends TowerType {
  var cost = 0
	var range = 4.0f
	var damage = 0.0f
	var fireRate = 10
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = WhirlpoolBottomID
	var projectileID = HarpoonID
	var speed = 2.0f
	var value = 20
	var basename = "Whirlpool Tower"
	var name = "Whirlpool Tower Level 1"
	var description = "Slowing tower\n  Placed below water, slows\n  enemies in area above water"
	var hotkey = 'W'
  init
}

class WhirlpoolTop(xc: Float, yc: Float) extends SlowingTower(xc, yc, WhirlpoolTop) {

}

object WhirlpoolTop extends SlowingTowerType {
  var cost = 0
	var range = 4.0f
	var damage = 0.0f
	var fireRate = 1
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = WhirlpoolTopID
	var projectileID = HarpoonID
	var speed = 0.0f
	var value = 0
	var basename = "Whirlpool Tower"
	var name = "Whirlpool Tower Level 1"
	var slowMult = 0.75f
	var slowTime = 20
	var description = "Slowing tower\n  Placed below water, slows\n  enemies in area above water"
	var hotkey = 'W'
  init
}

class MissileTower(xc: Float, yc: Float) extends Tower(xc, yc, MissileTower) {
	var numTargets = 3

	override def tick() : List[Projectile] = {
		if(nextShot == 0) {
			val enemies = map.aoe(r, c, range)
			if (!enemies.isEmpty) {
				nextShot = fireRate
				var projectiles = List[Projectile]()
				for(i <- 1 to numTargets) {
					if (!enemies.isEmpty) {
						val target = currAI.pick(r, c, enemies)
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
  var cost = 0
	var range = 2.0f
	var damage = 3.0f
	var fireRate = 40
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = MissileTowerID
	var projectileID = MissileID
	var speed = 0.3f
	var value = 50
	var basename = "Missile Tower"
	var name = "Missile Tower Level 1"
	var description = "Multitarget tower\n  Placed above water\n  Fires at multiple enemies\n  within range"
	var hotkey = 'M'
  init
}

class NetTower(xc: Float, yc: Float) extends Tower(xc, yc, NetTower) {


	override def tick(): List[Projectile] = {
		if(nextShot == 0) {
			val enemies = map.aoe(r, c, range)
			if (!enemies.isEmpty) {
				nextShot = fireRate
				val target = currAI.pick(r, c, enemies)
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
  var cost = 0
	var range = 2.0f
	var damage = 0.0f
	var fireRate = 90
	var aoe = 0.0f
	var currAI: AI = new RandomAI
	var id = NetTowerID
	var projectileID = NetID
	var speed = 0.5f
	var value = 100
	var basename = "Net Tower"
	var name = "Net Tower Level 1"
	var description = "Single target tower\n  Placed above water\n  Temporarily stops targeted\n  enemy from moving"
	var hotkey = 'N'
  init
}

class SteamTower(xc: Float, yc: Float) extends Tower(xc, yc, SteamTower) {

	override def tick(): List[Projectile] = {
		if(nextShot == 0) {
			var enemiesU = Set[Enemy]()
			var enemiesL = Set[Enemy]()
			var enemiesD = Set[Enemy]()
			var enemiesR = Set[Enemy]()

			for(i <- 1 to range.toInt) {
				map(r+i,c) match {
					case Some(tile) => enemiesU ++= tile.enemies
					case None => ()
				}
				map(r-i,c) match {
					case Some(tile) => enemiesD ++= tile.enemies
					case None => ()
				}
				map(r,c+i) match {
					case Some(tile) => enemiesR ++= tile.enemies
					case None => ()
				}
				map(r,c-i) match {
					case Some(tile) => enemiesL ++= tile.enemies
					case None => ()
				}
			}
			val enemies = enemiesU ++ enemiesL ++ enemiesR ++ enemiesD
			if (!enemies.isEmpty) {
				nextShot = fireRate
				val target = currAI.pick(r, c, enemiesU, enemiesD, enemiesL, enemiesR)
				var dir = Up
				if (target.r.toInt > r.toInt) {
					dir = Down
				} else if (target.r.toInt < r.toInt) {
					dir = Up
				} else if (target.c.toInt > c.toInt) {
					dir = Right
				} else {
					dir = Left
				}
				val proj = Projectile(r, c, dir, this)
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
  var cost = 0
	var range = 2.5f
	var damage = 5.0f
	var fireRate = 90
	var aoe = 1.0f
	var currAI: AI = new SteamRandomAI
	var id = SteamTowerID
	var projectileID = SteamID
	var speed = 1.0f
	var value = 30
	var basename = "Steam Tower"
	var name = "Steam Tower Level 1"
	var description = "Line damage tower\n  Placed below water\n  Damages all enemies in one\n  in one of four directions"
	var hotkey = 'S'
  init
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
