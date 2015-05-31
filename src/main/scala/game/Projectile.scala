package com.github.fellowship_of_the_bus
package tdtd
package game

import scala.math._

object Projectile {
  val width = 1.0f
  val height = 1.0f

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
    val rVec = tar.r - r
    val cVec = tar.c - c
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
      val theta = atan2(rVec, cVec)
      rotation = toDegrees(theta).asInstanceOf[Float] + 90f
      r += (rVec / dist) * speed
      c += (cVec / dist) * speed
      0
    }
  }
}

class HotWater(x: Float, y: Float, dir: Int, tower:Tower) extends Projectile(x, y, null, tower) {
  var time = 1000

  override def tick() = {
    time -= 1
    if (dir == 0) {
      c = c + speed
    } else if (dir == 1) {
      c = c - speed
    } else if (dir == 2) {
      r = r - speed
    } else {
      r = r + speed
    }

    var totalDmg = 0.0f
    var money = 0
    var kills = 0

    // val enemies = map.aoe(tar.r, tar.c, aoe)
    // for (e <- enemies) {
    //   var data = e.hit(dmg)
    //   totalDmg += data.dmg
    //   money += data.money
    //   if (data.money != 0) {
    //     kills += 1
    //   }
    // }
    // tower.kills += kills
    // tower.dmgDone += dmgDone
    inactivate
    money
  }
}

class Net(x: Float, y: Float, tar: Enemy, tower: Tower) extends Projectile(x, y, tar, tower) {
  override def tick = {
    val rVec = r - tar.r
    val cVec = c - tar.c
    val dist = sqrt((rVec * rVec) + (cVec * cVec)).asInstanceOf[Float]


    if (dist < speed) {
      val enemies = map.aoe(tar.r, tar.c, aoe)
        for (e <- enemies) {
          e.slow(new SlowEffect(0, 40))
        }
      inactivate
    } else {
      r += (rVec / dist) * speed
      c += (cVec / dist) * speed
    }
    0
  }
}