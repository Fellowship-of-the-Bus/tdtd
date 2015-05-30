package com.github.fellowship_of_the_bus
package tdtd
package game

import scala.math._

object Projectile {
  val width = 0.5f
  val height = 0.5f

  def apply(x: Float, y: Float, tar: Enemy, tower: Tower) = {
    new Projectile(x, y, tar, tower)
  }
}

class Projectile (x: Float, y: Float, val tar: Enemy, val tower:Tower) extends GameObject(x,y) {
  val width = Projectile.width
  val height = Projectile.height
  val dmg = tower.kind.damage
  val speed = tower.kind.speed
  val aoe = tower.kind.aoe
  val id = tower.kind.projectileID
  
  def tick() = {
    val rVec = r - tar.r
    val cVec = c - tar.c
    val dist = sqrt((rVec * rVec) + (cVec * cVec)).asInstanceOf[Float]
    var totalDmg = 0.0f
    var money = 0
    var kills = 0

    if (dist < speed) {
      val enemies = map.aoe(tar.r, tar.c, aoe)
      for (e <- enemies) {
        var data = e.hit(dmg)
        totalDmg += data.dmg
        money += data.money
        if (data.money != 0) {
          kills += 1
        }
      }
      // tower.kills += kills
      // tower.dmgDone += dmgDone
      inactivate
      money
    } else {
      r += (rVec / dist) * speed
      c += (cVec / dist) * speed
      0
    }
  }
}