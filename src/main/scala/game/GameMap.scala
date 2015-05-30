package com.github.fellowship_of_the_bus
package tdtd
package game

import IDMap._
import lib.game.GameConfig.{Height,Width}
import scala.collection.mutable.Set

class Coord (m: Int, n:Int){
    var c = m
    var r = n
}

object GameMap {
    val blocking = -1
    val occupied = 0
    val okay = 1
    val semiOccupied = 2
}

class GameMap (mapWidth: Int, mapHeight: Int, entranceC: Int, exitC: Int) {
    val entranceR = 0
    val exitR = mapHeight-1

    def this() = this(25,25,12,12)

    var map = new Array[Array[Tile]](mapHeight)
    for (r <- 0 to mapHeight-1) {
        map(r) = new Array[Tile](mapWidth)
        for (c <- 0 to mapWidth -1) {
            map(r)(c) = new Tile
        }
    }
    map(entranceR)(entranceC).entrance = true
    map(exitR)(exitC).exit = true
    map(exitR)(exitC).direction = 3 //down
    def apply(r: Float, c:Float) = {
        if (r.toInt < 0 || r.toInt > mapWidth-1
                        || c.toInt < 0
                        || c.toInt > mapHeight-1) {
            None
        }
        Some(map(r.toInt)(c.toInt))
    }
    def aoe(r:Float, c:Float, range:Float) = {
        var left = math.max(r-range,0).toInt
        var right = math.min(r+range, mapWidth-1).toInt
        var top = math.max(c-range,0).toInt
        var bottom = math.min(c+range,mapHeight-1).toInt
        var enemies = Set[Enemy]()
        for (m <- left to right) {
            for (n <- top to bottom) {
                enemies ++= map(m)(n).enemies
            }
        }
        enemies = enemies.filter(e => (e.r - r)*(e.r-r) + (e.c - c)*(e.c-c) <= r*r)
        enemies
    }
    //-1 = blocking, 0 = occupied, 1 = ok
    def placeable (r: Float, c:Float) : Int = {
        import GameMap._
        var tmp = map(r.toInt)(c.toInt)
        if (tmp.entrance || tmp.exit || tmp.occupied) {
            occupied
        }
        tmp.occupied = true
        var flag = dijkstras()
        tmp.occupied = false
        dijkstras()
        if (flag) {
            blocking
        } else {
            okay
        }
    }
    def placeTower (r: Float, c:Float, tower: Tower) = {
        var tmp = map(r.toInt)(c.toInt)
        tmp.placeTower(tower)
        dijkstras()
        tower.setMap(this)
    }
    def removable (r: Float, c:Float): Boolean = {
        var tmp = map(r.toInt)(c.toInt)
        tmp.occupied
    }
    def removeTower (r:Float, c:Float) = {
        var tmp = map(r.toInt)(c.toInt)
        tmp.removeTower()
    }
    def spawn (e: Enemy) = {
        e.r = entranceR
        e.c = entranceC
        map(entranceR)(entranceC).register(e)
        e.setMap(this)
    }
    def dijkstras () : Boolean = {
        var sourceR = exitR
        var sourceC = exitC
        var sourceTile = map(exitR)(exitC)
        map(sourceR)(sourceC).dist = 0
        var Q = Set[Coord]()
        for (r <- 0 to mapHeight-1) {
            for (c <- 0 to mapWidth-1) {
                if (r != sourceR || c != sourceC) {
                    map(r)(c).dist = Int.MaxValue
                }

                if (!map(r)(c).occupied)
                    Q += new Coord(r,c)
            }
        }
        while (!Q.isEmpty) {
            var curTileCoord = Q.minBy(x=>map(x.r)(x.c).dist)
            var curTile = map(curTileCoord.r)(curTileCoord.c)
            if (curTile.dist == Int.MaxValue) return true
            Q -= curTileCoord
            //for each neighbour
            if (curTileCoord.r-1 >=0) {
                var neighbour = map(curTileCoord.r-1)(curTileCoord.c)
                if (curTile.dist + 1 < neighbour.dist) {
                    neighbour.dist = curTile.dist +1
                    neighbour.direction = 3 // down
                }
            }
            if (curTileCoord.r+1 <= mapWidth-1) {
                var neighbour = map(curTileCoord.r+1)(curTileCoord.c)
                if (curTile.dist + 1 < neighbour.dist) {
                     neighbour.dist = curTile.dist +1
                     neighbour.direction = 2 // up
                }
            }
            if (curTileCoord.c-1 >=0) {
                var neighbour = map(curTileCoord.r)(curTileCoord.c-1)
                if (curTile.dist + 1 < neighbour.dist) {
                    neighbour.dist = curTile.dist +1
                    neighbour.direction = 1 // right
                }
            }
            if (curTileCoord.c+1 <= mapHeight-1) {
                var neighbour = map(curTileCoord.r)(curTileCoord.c+1)
                if (curTile.dist + 1 < neighbour.dist) {
                    neighbour.dist = curTile.dist +1
                    neighbour.direction = 0 // left
                }
            }
        }
        false
    }
    override def toString() : String = {
        var output = ""
        for (r <- 0 to mapHeight) {
            for (c <- 0 to mapWidth) {
            }
        }
        output
    }

}
