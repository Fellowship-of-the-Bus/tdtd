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
        }

        val tower = tile.tower
        if (! tower.isEmpty) {
          val t = tower.get
          val (tx, ty) = convert(t)
          drawObject(t, g)
        }
      }
    }

    for (p <- game.projectiles; if (p.active)) {
      if (p.getMap == map) {
        val (px, py) = convert(p)
        drawObject(p, g)
      }
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
            case Some(t) => TowerSelection(t)
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
            if (game.placeTower ( Tower(t,r,c) ,r,c, layer) == okay){
              GameUI.placeSelection = 0
            }
          }
          case DepthChargeTowerID => {
            val tower = Tower(t,r,c)
            if (game.placeTower ( tower ,r,c, layer) == okay) {
              tower.setMap(game.map(BottomLayer))
              GameUI.placeSelection = 0
            }
          }
          case OilDrillTowerID => {
            if (game.placeTower ( Tower(t,r,c) ,r,c,BothLayers) == okay) {
              GameUI.placeSelection = 0
            }
          }
          case _ => ()
            
        }
      } else if (Layer.layer2Int(layer) == 1) { //bottomlayer
        t match {
          case SteamTowerID |
               HarpoonTowerID => {
            if (game.placeTower(Tower(t,r,c),r,c,layer) == okay) {
              GameUI.placeSelection = 0
            }
          }
          case TorpedoTowerID => {
            val tower = Tower(t,r,c)
            if (game.placeTower(tower,r,c,layer) == okay) {
              tower.setMap(game.map(TopLayer))
              GameUI.placeSelection = 0
            }
          }
          case OilDrillTowerID => {
            if(game.placeTower(Tower(t,r,c),r,c,BothLayers) == okay) {
              
            }
          }
          case WhirlpoolBottomID => {
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
            }
          }
          case IceTowerBottomID => {
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
