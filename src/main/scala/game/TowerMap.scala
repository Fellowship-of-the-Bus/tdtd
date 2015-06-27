package com.github.fellowship_of_the_bus
package tdtd
package game

import lib.ui.{Image, Animation, Drawable}
import IDMap._
import scala.io._
import java.io._
import java.util.Scanner

class Attribute(
  val id: Int,
  val level: Int,
  val dmg: Float,
  val rate: Int,
  val range: Float,
  val aoe: Float,
  val projspd: Float,
  val cost: Int,
  val slow: Float,
  val money: Int
  ) {}

object TowerMap {
  var towerMap = Map[(Int, Int), Attribute]()
  val stringMap = Map(
    "harpoon" -> HarpoonTowerID,
    "cannon" -> CannonTowerID,
    "torpedo" -> TorpedoTowerID,
    "oildrill" -> OilDrillTowerID,
    "icebottom" -> IceTowerBottomID,
    "depthC" -> DepthChargeTowerID,
    "whirlt" -> WhirlpoolTopID,
    "whirlb" -> WhirlpoolBottomID,
    "missile" -> MissileTowerID,
    "net" -> NetTowerID,
    "steam"-> SteamTowerID
  )

  val towerFile = "data/Tower.csv"
  val classFile = "TowerMap.class"
  val f = new File(".")

  println (f.getAbsolutePath())
  val tmp = TDTD.getClass.getClassLoader().getResourceAsStream(towerFile)
  println(tmp)
  val file = new File(towerFile)
  val s = new Scanner(tmp)
  while (s.hasNextLine) {
//  for (line <- Source.fromFile(towerFile).getLines()) {
    val line = s.nextLine()
    var strings = line.split(',')
    if(strings(0) != "tower") {
      var att = new Attribute(stringMap(strings(0)),
                      strings(1).toInt,
                      strings(2).toFloat,
                      strings(3).toInt,
                      strings(4).toFloat,
                      strings(5).toFloat,
                      strings(6).toFloat,
                      strings(7).toInt,
                      strings(8).toFloat,
                      strings(9).toInt)
      towerMap = towerMap + ((att.id, att.level) -> att)
    }
  }
}
