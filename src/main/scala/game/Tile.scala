package com.github.fellowship_of_the_bus
package tdtd
package game

import IDMap._
import lib.game.GameConfig.{Height,Width}
import scala.collection.mutable.Set

class Tile {
    var tower : Option[Tower] = None
    var direction = 0
    var dist = 0
    var enemies = Set[Enemy]()
    var occupied = false
    var pathable = true
    var entrance = false
    var exit = false

    def register(e: Enemy) = {
        enemies += e
    }
    def deregister(e: Enemy) = {
        enemies -= e
    }
    def placeTower(t: Tower) = {
        if (t.id != WhirlpoolTopID) {
          pathable = false
        }

/*        tower.id match {
          case HarpoonTowerId => pathable = false
          case OilDrillTowerID => pathable = false
          case CannonTowerID => pathable = false
          case IceTowerTowerID => pathable = false
          case DepthChargeTowerID => pathable = false
          case WhilrpoolTopID => pathable = true
          case MissileTowerID => pathable = false
          case NetTowerTowerID => pathable = false
          case TorpedoTowerID => pathable = false
          case IceTowerBottomID => pathable = false
          case WhirlpoolBottomID => pathable = false
          case SteamTowerID => pathable = false
        }*/
        tower = Some(t)
        occupied = true
    }
    def removeTower() = {
        pathable = true
        tower = None
        occupied = false
    }
    def getTower() = {
        tower
    }
    def ==(that: Tile) {
        direction == that.direction
    }
}
