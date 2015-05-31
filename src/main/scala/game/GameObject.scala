package com.github.fellowship_of_the_bus
package tdtd
package game
import IDMap._
import lib.game.GameConfig.{Width}

abstract class GameObject(xc: Float, yc: Float) {
  var r = xc
  var c = yc
  var rotation = 0f

  protected var map : GameMap = null

  def id: Int
  def width: Float
  def height: Float

  private var isActive = true
  def active = isActive
  def inactivate = isActive = false

  def topLeftCoord = (r-width/2, c-height/2)
  def setMap (m: GameMap) = {
      map = m
  }

  def getMap(): GameMap = map
}
