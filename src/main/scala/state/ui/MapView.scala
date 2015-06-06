package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import org.newdawn.slick.{GameContainer, Graphics, Color}
import org.newdawn.slick.state.{StateBasedGame}
import org.newdawn.slick.geom.Rectangle

import GameUI.Dimensions._
import game._
import game.Layer._
import game.IDMap._
import game.GameMap._

import lib.ui.Drawable

class MapView(x: Float, y: Float, width: Float, height: Float, val layer: Layer, gameArea : GameArea)(implicit bg: Color) extends Pane(x, y, width, height) {
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
    convert(r - (0.5f * go.width), c - (0.5f * go.height))
  }

  def drawScaledImage(im: Drawable, x: Float, y: Float, width: Float, height: Float, g: Graphics) = {
    val scaleX = width*widthRatio/im.getWidth
    val scaleY = height*heightRatio/im.getHeight
    g.scale(scaleX,scaleY)

    im.draw(x * im.getWidth/width/widthRatio, y*im.getHeight/height/heightRatio)
  
    g.scale(1/scaleX, 1/scaleY)
  }

  def drawObject(e: GameObject, g: Graphics) {
    var image = IDMap.images(e.id)
    var (ex, ey) = convert(e)

    val exPos = ex + 0.5f * (e.width * widthRatio)
    val eyPos = ey + 0.5f * (e.height * heightRatio)

    g.translate(exPos, eyPos)
    g.rotate(0 , 0 , e.rotation)
    g.translate(-exPos, -eyPos)

    drawScaledImage(IDMap.images(e.id), ex, ey, e.width, e.height, g)
    
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
    
    val (entranceX, entranceY) = convert(map.entranceR, map.entranceC)
    val (exitX, exitY) = convert(map.exitR, map.exitC)

    g.setColor(new Color(0,0,120, (0.3*255).asInstanceOf[Int]))
    g.fillRect(entranceX, entranceY, widthRatio, heightRatio)
    g.setColor(new Color(0,0,200, (0.3*255).asInstanceOf[Int]))
    g.fillRect(exitX, exitY, widthRatio, heightRatio)

    drawScaledImage(images(DirectionArrowID), entranceX, entranceY, 1.0f, 1.0f, g)
    drawScaledImage(images(DirectionArrowID), exitX, exitY, 1.0f, 1.0f, g)
            
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
    g.setClip(new Rectangle(absoluteX, absoluteY, width, height))
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
    g.clearClip()
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
