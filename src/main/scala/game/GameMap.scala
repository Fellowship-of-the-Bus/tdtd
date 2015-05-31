package com.github.fellowship_of_the_bus
package tdtd
package game

import IDMap._
import lib.game.GameConfig.{Height,Width}
import scala.collection.mutable.Set
import GameMap._

class Coord (m: Int, n:Int){
    var r = m
    var c = n

    override def toString = {
        "(" + r.toString + "," + c.toString +")"
    }
}

object GameMap {
    val blocking = -1
    val occupied = 0
    val okay = 1
    val semiOccupied = 2
    val Left = 0
    val Right = 1
    val Up = 2
    val Down = 3

    val defaultWidth = 13
    val defaultHeight = 13
}

class GameMap (val mapWidth: Int, val mapHeight: Int, val entranceC: Int, val exitC: Int) {
    val entranceR = 0
    val exitR = mapHeight-1

    def this() = this(defaultWidth, defaultHeight,6,6)

    var map = new Array[Array[Tile]](mapHeight)
    for (r <- 0 to mapHeight-1) {
        map(r) = new Array[Tile](mapWidth)
        for (c <- 0 to mapWidth -1) {
            map(r)(c) = new Tile
        }
    }
    map(entranceR)(entranceC).entrance = true
    map(exitR)(exitC).exit = true
    map(exitR)(exitC).direction = Down //down
    dijkstras
    def getTile (r: Float, c:Float) = {
        map((r+0.5f).toInt)((c+0.5f).toInt)
    }
    def apply(r: Float, c:Float) = {
        if ((r+0.5f).toInt < 0 || (r+0.5f).toInt > mapWidth-1
                        || (c+0.5f).toInt < 0
                        || (c+0.5f).toInt > mapHeight-1) {
            None
        } else {
            Some(map((r+0.5f).toInt)((c+0.5f).toInt))
        }
    }
    def aoe(r:Float, c:Float, range:Float) = {
        var left = (math.max(r-range,0)+0.5f).toInt
        var right = (math.min(r+range, mapWidth-1)+0.5f).toInt
        var top = (math.max(c-range,0)+0.5f).toInt
        var bottom = (math.min(c+range,mapHeight-1)+0.5f).toInt
        var enemies = Set[Enemy]()
        for (m <- left to right) {
            for (n <- top to bottom) {
                enemies ++= map(m)(n).enemies
            }
        }
        enemies = enemies.filter(e => (e.r - r)*(e.r-r) + (e.c - c)*(e.c-c) <= range*range)
        enemies
    }
    def placeable (r: Float, c:Float) : Int = {
        import GameMap._
        var tmp = getTile(r,c)
        if (tmp.entrance || tmp.exit || tmp.occupied || !tmp.enemies.isEmpty) {
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
        var tmp = getTile(r,c)
        tmp.placeTower(tower)
        dijkstras()
        tower.setMap(this)
    }
    def removable (r: Float, c:Float): Boolean = {
        var tmp = getTile(r,c)
        tmp.occupied
    }
    def removeTower (r:Float, c:Float) = {
        var tmp = getTile(r,c)
        tmp.removeTower()
    }
    def spawn (e: Enemy) = {
        e.r = entranceR// - 0.4f
        e.c = entranceC
        map(entranceR)(entranceC).register(e)
        e.setMap(this)
    }
    def dijkstras () : Boolean = {
        var sourceR = exitR
        var sourceC = exitC
        var sourceTile = map(exitR)(exitC)
        var Q = Set[Coord]()
        for (r <- 0 to mapHeight-1) {
            for (c <- 0 to mapWidth-1) {
                map(r)(c).dist = Int.MaxValue/2

                if (map(r)(c).pathable) {
                    Q += new Coord(r,c)
                }
            }
        }

        sourceTile.dist = 0
        while (!Q.isEmpty) {
            var curTileCoord = Q.minBy(x=>map(x.r)(x.c).dist)
            var curTile = map(curTileCoord.r)(curTileCoord.c)
            if (curTile.dist == Int.MaxValue/2) {
                return true
            }
            Q -= curTileCoord

            if (curTile.pathable) {
                //for each neighbour
                if (curTileCoord.r-1 >=0) {
                    var neighbourCoord = new Coord(curTileCoord.r-1,curTileCoord.c)
                    var neighbour = map(curTileCoord.r-1)(curTileCoord.c)
                    if (curTile.dist + 1 < neighbour.dist) {
                        neighbour.dist = curTile.dist +1
                        neighbour.direction = Down // down
                    }
                }
                if (curTileCoord.r+1 <= mapHeight-1) {
                    var neighbourCoord = new Coord(curTileCoord.r+1,curTileCoord.c)
                    var neighbour = map(curTileCoord.r+1)(curTileCoord.c)
                    if (curTile.dist + 1 < neighbour.dist) {
                        neighbour.dist = curTile.dist +1
                        neighbour.direction = Up // up
                    }
                }
                if (curTileCoord.c-1 >=0) {
                    var neighbourCoord = new Coord(curTileCoord.r,curTileCoord.c-1)
                    var neighbour = map(curTileCoord.r)(curTileCoord.c-1)
                    if (curTile.dist + 1 < neighbour.dist) {
                        neighbour.dist = curTile.dist +1
                        neighbour.direction = Right // right
                    }
                }
                if (curTileCoord.c+1 <= mapWidth-1) {
                    var neighbourCoord = new Coord(curTileCoord.r,curTileCoord.c+1)
                    var neighbour = map(curTileCoord.r)(curTileCoord.c+1)
                    if (curTile.dist + 1 < neighbour.dist) {
                        neighbour.dist = curTile.dist +1
                        neighbour.direction = Left // left
                    }
                }
            }
        }
        false
    }
    override def toString() : String = {
        var output = "a\n"
        for (r <- 0 to mapHeight -1 ){
            for (c <- 0 to mapWidth-1) {
                if (map(r)(c).occupied) {
                    output += "X|"
                } else {
                    map(r)(c).direction match {
                        case Up => output += "^|"
                        case Down => output += "v|"
                        case Left => output += "<|"
                        case Right => output += ">|"
                    }
                }
            }
            output += "\n"
        }
        output += "\n"
        for (r <- 0 to mapHeight -1) {
            for (c <- 0 to mapWidth -1) {
                if (map(r)(c).occupied) {
                    output += "X|"
                } else {
                    output += map(r)(c).dist.toString + "|"
                }
            }
            output += "\n"
        }
        output
    }

}
