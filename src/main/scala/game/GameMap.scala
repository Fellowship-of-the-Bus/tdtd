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

class GameMap {
    val mapWidth = 25
    val mapHeight = 25
    val entranceC = 12
    val entranceR = 0
    val exitC = 12
    val exitR = mapHeight-1

    var map = new Array[Array[Tile]](mapWidth)
    for (i <- 0 to mapWidth-1) {
        map(i) = new Array[Tile](mapHeight)
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
        enemies = enemies.filter(e=> (e.x - x)*(e.x-x) + (e.y - y)*(e.y-y) <= r*r)
        enemies
    }
    def placable (r: Float, c:Float) : Boolean = {
        var tmp = map(r.toInt)(c.toInt)
        !tmp.occupied
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
    def dijkstras () = {
        var sourceR = exitR
        var sourceC = exitC
        var sourceTile = map(exitR)(exitC)
        map(sourceR)(sourceC).dist = 0
        var Q = Set[Coord]()
        for (m <- 0 to mapWidth-1) {
            for (n <- 0 to mapHeight-1) {
                if (m != sourceR || n != sourceC) {
                    map(m)(n).dist = 25000
                }
                Q += new Coord(m,n)
            }
            while (!Q.isEmpty) {
                var curTileCoord = Q.minBy(x=>map(x.r)(x.c).dist)
                var curTile = map(curTileCoord.r)(curTileCoord.c)
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
      
        }
    }

}
