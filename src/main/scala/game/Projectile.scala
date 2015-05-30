package com.github.fellowship_of_the_bus
package tdtd
package game

import scala.math._

object Projectile {
  val width = 10
  val height = 10

  def apply(x: Int, y: Int, e: Enemy, tower: Tower) = {
    new Projectile(x, y, e, tower)
  }
}

abstract class Projectile (x: Float, y: Float, e: Enemy, t:Tower) extends GameObject(x,y) {
  val width = Projectile.width
  val height = Projectile.height
  val dmg = t.kind.damage
  val speed = t.kind.speed
  val aoe = t.kind.aoe

  def tick() {
    val xVec = x - e.x
    val yVec = y - e.y
    val dist = sqrt((xVec * xVec) + (yVec * yVec))
    var totalDmg = 0.0f

    if (dist < speed) {
      // enemies = get enemies in AoE
      for (e <- enemies) {
        totalDmg += e.hit(dmg)
      }
      inactivate
    } else {
      x += (xVec / dist) * speed
      y += (yVec / dist) * speed
    }
  }
}