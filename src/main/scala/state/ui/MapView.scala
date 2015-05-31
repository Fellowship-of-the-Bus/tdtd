package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}

import GameUI.Dimensions._
import game._
import game.Layer._
import game.IDMap._
import game.GameMap._

class MapView(x: Float, y: Float, width: Float, height: Float, layer: Layer, gameArea : GameArea)(implicit bg: Color) extends Pane(x, y, width, height) {
  def this(x: Float, y: Float, layer: Layer, gameArea: GameArea)(implicit bg: Color) = this(x, y, mapWidth, mapHeight, layer, gameArea)

  var map: GameMap = null
  var mapInput = new MapInput(0,0, width, height, place, this, absoluteX, absoluteY)
  val widthRatio = width / GameMap.defaultWidth
  val heightRatio = height / GameMap.defaultHeight

  def convert(r: Float, c: Float) = {
    (c * widthRatio, r * heightRatio)
  }

  def convert(go: GameObject): (Float, Float) = {
    val (r,c) = (go.r, go.c)//go.topLeftCoord
    convert(r, c)
  }

  def drawObject( e:GameObject, g:Graphics) {
    var image = IDMap.images(e.id)
    val (ex, ey) = convert(e)
    val exPos = ex + 0.5f * (e.width * widthRatio)
    val eyPos = ey + 0.5f * (e.height * heightRatio)

    g.translate(exPos, eyPos)
    g.rotate(0 , 0 , e.rotation)
    g.translate(-exPos, -eyPos)

    val scaleX = e.width*widthRatio/image.getWidth
    val scaleY = e.height*heightRatio/image.getHeight
    g.scale(scaleX,scaleY)
    
    IDMap.images(e.id).draw(ex * image.getWidth/e.width/widthRatio, ey*image.getHeight/e.height/heightRatio)
    
    // g.scale(image.getWidth/e.width/widthRatio,image.getHeight/e.height/heightRatio)
    g.scale(1/scaleX, 1/scaleY)
    g.translate(exPos, eyPos)
    g.rotate(0 , 0 , -e.rotation)
    g.translate(-exPos, -eyPos)
  }
  
  def drawOffset( e:GameObject, g:Graphics) {
    g.translate(-mapWidth, 0)
    drawObject(e, g)
    g.translate(mapWidth, 0)
  }

  override def draw(gc: GameContainer, sbg: StateBasedGame, g: Graphics): Unit = {
    super.draw(gc, sbg, g)

    g.setColor(Color.black)
    for (r <- 0 until map.mapHeight) {
      for (c <- 0 until map.mapWidth) {
        val (x, y) = convert(r, c) 
        g.drawLine(x, 0, x, height)
        g.drawLine(0, y, width, y)

        val tile = map(r, c).get
        val enemies = tile.enemies
        for (e <- enemies; if (e.active)) {
          val (ex, ey) = convert(e)
          e.draw(g, ex, ey)
          drawObject(e, g)
          if (Layer.layer2Int(layer) == 1) {
            drawOffset(e, g)
          }
        }

        val tower = tile.tower
        if (! tower.isEmpty) {
          val t = tower.get
          val (tx, ty) = convert(t)
          drawObject(t, g)
          if (Layer.layer2Int(layer) == 1) {
            drawOffset(t, g)
          }
        }
      }
    }

    for (p <- game.projectiles; if (p.active)) {
      if (p.getMap == map) {
        val (px, py) = convert(p)
        drawObject(p, g)
        if (Layer.layer2Int(layer) == 1) {
            drawOffset(p, g)
        }
      }
    }

    if (Layer.layer2Int(layer) == 1) {
        g.setColor(new Color(0, 99, 0xcc, 50))
        g.fillRect(0,0,mapWidth,mapHeight)
    }

    GameUI.displaySelection match {
      case TowerSelection(t) => 
        if (t.getMap == map || t.id == TorpedoTowerID) {
          val rx = t.kind.range * widthRatio
          val ry = t.kind.range * heightRatio
          var (tx, ty) = convert(t)
          tx += 0.5f * widthRatio
          ty += 0.5f * heightRatio
          g.setColor(new Color(0, 99, 0, 50))
          g.fillOval(tx-rx,ty-ry,rx*2,ry*2)
          g.setColor(new Color(0, 99, 0, 255))
          g.drawOval(tx-rx,ty-ry,rx*2,ry*2)
        }

      case _ => 0
    }

    mapInput.render(g)
  } 

  def place(r:Float, c:Float) {
    var t = GameUI.placeSelection
    if (t == NoTowerID) {
      if (map(r,c).isEmpty) {
        GameUI.displaySelection = NoSelection
      } else {
        if (map(r,c).get.getTower.isEmpty) {
          GameUI.displaySelection = NoSelection
        }else {
          GameUI.displaySelection = map(r,c).get.getTower match {
            case Some(tower) => {
              var tow = tower
              if (tower.isInstanceOf[MazingTower]) {
                if (map == game.map(TopLayer)) {
                  tow = game.map(BottomLayer)(r,c).get.getTower.get
                } else {
                  tow = game.map(TopLayer)(r,c).get.getTower.get
                }
              }
              TowerSelection(tow)
            }
            case None => NoSelection
          }
        }
      }
    } else {
      if (Layer.layer2Int(layer) == 0) { //topLayer
        t match {
          case HarpoonTowerID |
               CannonTowerID  |
               MissileTowerID |
               NetTowerID => {
            val tower = Tower(t,r,c)
            if (game.placeTower ( tower ,r,c, layer) == okay){
              GameUI.placeSelection = 0
              GameUI.displaySelection = TowerSelection(tower)

            }
          }
          case DepthChargeTowerID => {
            val tower = Tower(t,r,c)
            if (game.placeTower ( tower ,r,c, layer) == okay) {
              tower.setMap(game.map(BottomLayer))
              GameUI.placeSelection = 0
              GameUI.displaySelection = TowerSelection(tower)
            }
          }
          case OilDrillTowerID => {
            val tower = Tower(t,r,c)
            if (game.placeTower ( tower ,r,c,BothLayers) == okay) {
              GameUI.placeSelection = 0
              GameUI.displaySelection = TowerSelection(tower)
            }
          }
          case _ => ()
            
        }
      } else if (Layer.layer2Int(layer) == 1) { //bottomlayer
        t match {
          case SteamTowerID |
               HarpoonTowerID => {
            val tower = Tower(t,r,c)
            if (game.placeTower(Tower(t,r,c),r,c,layer) == okay) {
              GameUI.placeSelection = 0
              GameUI.displaySelection = TowerSelection(tower)
            }
          }
          case TorpedoTowerID => {
            val tower = Tower(t,r,c)
            if (game.placeTower(tower,r,c,layer) == okay) {
              tower.setMap(game.map(TopLayer))
              GameUI.placeSelection = 0
              GameUI.displaySelection = TowerSelection(tower)
            }
          }
          case OilDrillTowerID => {
            val tower = Tower(t,r,c)
            if(game.placeTower(tower,r,c,BothLayers) == okay) {
              GameUI.placeSelection = 0
              GameUI.displaySelection = TowerSelection(tower)
            }
          }
          case WhirlpoolBottomID => {
            val tower = Tower(t,r,c)
            if (game.map(Layer.layer2Int(TopLayer))(r,c).get.getTower.isEmpty &&
                game.map(Layer.layer2Int(BottomLayer)).placeable(r,c) == okay ){
              var whirltop = Tower(WhirlpoolTopID, r, c)
              var whirlbottom = Tower(t,r,c)
              game.map(Layer.layer2Int(TopLayer)).placeTower(r,c, whirltop)
              game.map(Layer.layer2Int(BottomLayer)).placeTower(r,c,whirlbottom)
              game.spend(whirlbottom.kind.value)
              game.addTowerToList(whirltop)
              game.addTowerToList(whirlbottom)
              GameUI.placeSelection = 0
              GameUI.displaySelection = TowerSelection(tower)
            }
          }
          case IceTowerBottomID => {
            val tower = Tower(t,r,c)
            if (game.map(Layer.layer2Int(TopLayer)).placeable(r,c) == okay &&
                game.map(Layer.layer2Int(BottomLayer)).placeable(r,c) == okay) {
              var icetop = Tower(IceTowerTopID,r,c)
              var icebottom = Tower(t,r,c)
              game.map(Layer.layer2Int(TopLayer)).placeTower(r,c,icetop)
              game.map(Layer.layer2Int(BottomLayer)).placeTower(r,c,icebottom)
              game.spend(icebottom.kind.value)
              game.addTowerToList(icetop)
              game.addTowerToList(icebottom)
              GameUI.placeSelection = 0
              GameUI.displaySelection = TowerSelection(tower)
            }
          }
          case _ => ()   
        }
      }
    }
  }

  override def init(gc: GameContainer, sbg: StateBasedGame) = {
    mapInput = new MapInput(0,0, width, height, place, this, absoluteX, absoluteY)
    mapInput.setInput(gc.getInput)
    mapInput.init(gc, sbg)

    super.init(gc, sbg)
  }

  override def reset() = {
    map = game.getMap(layer)
  }
}
