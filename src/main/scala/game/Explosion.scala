package com.github.fellowship_of_the_bus
package tdtd
package game

import IDMap._

class Explosion(val r: Float, val c: Float, val aoe: Float, val map: GameMap) {
	val id = ExplosionID
	val maxTime = 15f
	var time = maxTime
	var size = 0f

	private var isActive = true
  def active = isActive
  def inactivate() = isActive = false

  def tick() = {
  	time -= 1;
  	size += aoe / maxTime

  	if (time <= 0) {
  		inactivate
  	}
  }

  def getMap(): GameMap = map
}
