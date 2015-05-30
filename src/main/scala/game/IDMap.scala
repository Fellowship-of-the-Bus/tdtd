package com.github.fellowship_of_the_bus
package tdtd.game

import ui.{Image, Animation}

object IDMap{

  val FotBLogoID = 1000
  val GameOverID = 1001
  val HarpoonTowerID = 2000
  val CannonTowerID = 2001
  val TorpedoTowerID = 2002
  val OilDrillTowerID = 2003
  val IceTowerBottomID = 2004
  val IceTowerTopID = 2005
  val DepthChargeTowerID = 2006
  val WhirlpoolTopID = 2007
  val WhirlpoolBottomID = 2008
  val MissileTowerID = 2009

  val FishID = 3001
  val CrabID = 3002
  val JellyfishID = 3003
  val SquidID = 3004
  val WhaleID = 3005
  val SharkID = 3006
  val MegalodonID = 3007


  val DolphinID = 3107
  val PenguinID = 3108
  val AlligatorID = 3109
  val HippoID = 3110
  val TurtleID = 3111
  val HydraID = 3112
  val KrakenID = 3113

  val imageMap = Map(
    FotBLogoID -> "img/FotB-Logo.png",
    GameOverID -> "img/GameOver.png",
    12345 -> Array("img/GameOver.png", "img/FotB-Logo.png")
  )

  lazy val images: Map[Int, ui.Drawable] = imageMap.map { x =>
    val (id, loc) = x
    val img = loc match {
      case xs: Array[String] => Animation(xs)
      case str: String => Image(str)
    }
    id -> img
  }
}
