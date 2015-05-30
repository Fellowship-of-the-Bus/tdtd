package com.github.fellowship_of_the_bus
package tdtd
package game

import scala.math._

object Projectile {
  val width = 10
  val height = 10

  def apply(x: Float, y: Float, e: Enemy, tower: Tower) = {
    new Projectile(x, y, e, tower)
  }
}

class Projectile (x: Float, y: Float, e: Enemy, t:Tower) extends GameObject(x,y) {
  val width = Projectile.width
  val height = Projectile.height
  val dmg = t.kind.damage
  val speed = t.kind.speed
  val aoe = t.kind.aoe

  def tick() {
    val rVec = r - e.r
    val cVec = c - e.c
    val dist = sqrt((rVec * rVec) + (cVec * cVec))
    var totalDmg = 0.0f

    if (dist < speed) {
      // enemies = get enemies in AoE
      // for (e <- enemies) {
      //   totalDmg += e.hit(dmg)
      // }
      inactivate
    } else {
      r += (rVec / dist) * speed
      c += (cVec / dist) * speed
    }
  }
}