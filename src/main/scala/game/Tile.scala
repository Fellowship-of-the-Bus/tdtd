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
    def ==(that: Tile) = direction == that.direction
}
