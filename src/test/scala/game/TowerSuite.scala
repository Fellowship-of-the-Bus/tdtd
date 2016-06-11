package com.github.fellowship_of_the_bus
package tdtd
package game

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import IDMap._
import Enemy._
import scala.collection.mutable.Set
import Tower._

@RunWith(classOf[JUnitRunner])
class TowerSuite extends FunSuite {

	test("Tower descriptions") {
		val t1 = Tower(HarpoonTowerID, 1, 1)
		val t2 = Tower(IceTowerBottomID, 1, 1)
		val t3 = Tower(CannonTowerID, 1, 1)
		val t4 = Tower(OilDrillTowerID, 1, 1)
		val l1 = List[String](
				"Value: 5",
				"Damage: 1.0",
				"Fire Rate: 1.0 seconds",
				"Range: 4.0",
				"Current AI: Random",
				"Kills: 0",
				"Damage Dealt: 0.0"
			)
		val l2 = List[String](
				"Value: 15",
				"Slow Multiplier: 75%",
				"Slow Time: 0.5 seconds",
				"Range: 4.0"
			)
		val l3 = List[String](
				"Value: 20",
				"Damage: 5.0",
				"Fire Rate: 2.0 seconds",
				"Range: 4.0",
				"Area of Effect: 2.0",
				"Current AI: Closest to Goal",
				"Kills: 0",
				"Damage Dealt: 0.0"
			)
		val l4 = List[String](
				"Value: 50",
				"Cash Earned per Round: 5"
			)
		// assert(t1.describe() === l1)
		// assert(t2.describe() === l2)
		// assert(t3.describe() === l3)
		// assert(t4.describe() === l4)
	}

}
