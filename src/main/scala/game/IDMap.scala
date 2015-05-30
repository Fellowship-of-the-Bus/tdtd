package com.github.fellowship_of_the_bus
package tdtd.game

import ui.{Image, Animation}

object IDMap {

  val FotBLogoID = 1000
  val GameOverID = 1001

  // Towers
  val HarpoonTowerID = 2000
  val OilDrillTowerID = 2001
  
  val CannonTowerID = 2100
  val IceTowerTopID = 2101
  val DepthChargeTowerID = 2102
  val WhirlpoolTopID = 2103
  val MissileTowerID = 2104
  val NetTowerID = 2105

  val TorpedoTowerID = 2200
  val IceTowerBottomID = 2201
  val WhirlpoolBottomID = 2202
  val SteamTowerID = 2203
  
  //Enemies
  val UnderStart = 3001
  val FishID = 3001
  val CrabID = 3002
  val JellyfishID = 3003
  val SquidID = 3004
  val WhaleID = 3005
  val SharkID = 3006
  val MegalodonID = 3007
  val UnderEnd = 3007

  val TopStart = 3107
  val DolphinID = 3107
  val PenguinID = 3108
  val AlligatorID = 3109
  val HippoID = 3110
  val TurtleID = 3111
  val HydraID = 3112
  val KrakenID = 3113
  val TopEnd = 3113

  // Projectiles
  val HarpoonID = 4000

  val CannonBallID = 4100
  val DepthChargeID = 4102
  val MissileID = 4104
  val NetID = 4105

  val TorpedoID = 4200
  val SteamID = 4203


  val imageMap = Map(
    FotBLogoID -> "img/FotB-Logo.png",
    GameOverID -> "img/GameOver.png",
    
    HarpoonTowerID -> "img/HarpoonTower.png",
    //OilDrillTowerID -> "img/OilDrillTower.png",
  
    CannonTowerID -> "img/CannonTower.png",
    IceTowerTopID -> "img/IceTowerTop.png",
    DepthChargeTowerID -> "img/DepthChargeTower.png",
    WhirlpoolTopID -> "img/Whirlpool.png",
    MissileTowerID -> "img/MissileTower.png",
    NetTowerID -> "img/NetTower.png",

    TorpedoTowerID -> "img/TorpedoTower.png",
    IceTowerBottomID -> "img/IceTowerBottom.png",
    WhirlpoolBottomID -> "img/WhirlpoolTower.png",
    SteamTowerID -> "img/SteamTower.png",

    CrabID -> "img/Crab.png",
    FishID -> "img/fish.png",
    SharkID -> "img/Shark.png",
    WhaleID -> "img/Whale.png",

    AlligatorID -> "img/Alligator.png",
    TurtleID -> "img/Turtle.png",
    HydraID -> "img/Hydra.png",

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
