package com.github.fellowship_of_the_bus
package tdtd.game

import lib.ui.{Image, Animation, Drawable}

object IDMap {

  val NoTowerID = 0

  val FotBLogoID = 1000
  val GameOverID = 1001

  // Towers
  val EitherTStart = 2000
  val HarpoonTowerID = 2000
  val EitherTEnd = 2000

  val BothTStart = 2001
  val OilDrillTowerID = 2001
  val BothTEnd = 2001

  val TopTStart = 2100
  val CannonTowerID = 2100
  val DepthChargeTowerID = 2101
  val MissileTowerID = 2102
  val NetTowerID = 2103
  val TopTEnd = 2103

  val WhirlpoolTopID = 2110
  val IceTowerTopID = 2111

  val UnderTStart = 2200
  val TorpedoTowerID = 2200
  val IceTowerBottomID = 2201
  val WhirlpoolBottomID = 2202
  val SteamTowerID = 2203
  val UnderTEnd = 2203

  val TIDRanges = Array(TopTStart, TopTEnd, EitherTStart, EitherTEnd, BothTStart, BothTEnd, UnderTStart, UnderTEnd)
  
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

  val CannonballID = 4100
  val DepthChargeID = 4102
  val MissileID = 4104
  val NetID = 4105

  val TorpedoID = 4200
  val SteamID = 4203

  val FastForwardOnID = 9000
  val FastForwardOffID = 9001

  val imageMap = Map(
    FotBLogoID -> "img/FotB-Logo.png",
    GameOverID -> "img/GameOver.png",
    
    HarpoonTowerID -> "img/HarpoonTower.png",
    OilDrillTowerID -> "img/OilTop.png",
  
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
    FishID -> "img/Fish.png",
    SharkID -> "img/Shark.png",
    WhaleID -> "img/Whale.png",
    //temporary start
    JellyfishID -> "img/Alligator.png",
    SquidID -> "img/Squid.png",
    MegalodonID -> "img/Shark.png",
    //temporary end

    AlligatorID -> "img/Alligator.png",
    TurtleID -> "img/Turtle.png",
    HydraID -> "img/Hydra.png",
    //temporary start
    HippoID -> "img/Whale.png",
    DolphinID -> "img/Dolphin.png",


    HarpoonID -> "img/Harpoon.png",
    CannonballID -> "img/Cannonball.png",
    MissileID -> "img/Missile.png",
    NetID -> "img/Net.png",
    SteamID -> "img/Steam.png",
    TorpedoID -> "img/Torpedo.png",

    FastForwardOnID -> "img/FastForward.png",
    FastForwardOffID -> "img/FastForwardOff.png",

    12345 -> Array("img/GameOver.png", "img/FotB-Logo.png")
  )

  lazy val images: Map[Int, Drawable] = imageMap.map { x =>
    val (id, loc) = x
    val img = loc match {
      case xs: Array[String] => Animation(xs)
      case str: String => Image(str)
    }
    id -> img
  }
}
