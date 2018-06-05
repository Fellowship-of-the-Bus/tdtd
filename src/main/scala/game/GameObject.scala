package com.github.fellowship_of_the_bus
package tdtd
package game
import IDMap._
import lib.game.GameConfig.{Width}
import lib.game.CenteredCoordinates

abstract class GameObject(xc: Float, yc: Float) extends CenteredCoordinates {
  var r = xc
  var c = yc
  def x = r
  def y = c
  var rotation = 0f

  protected var map : GameMap = null

  def id: Int
  def width: Float
  def height: Float

  private var isActive = true
  def active = isActive
  def inactivate() = isActive = false

  def setMap (m: GameMap) = {
      map = m
  }

  def getMap(): GameMap = map
}
