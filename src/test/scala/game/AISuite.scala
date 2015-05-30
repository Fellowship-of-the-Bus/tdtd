package com.github.fellowship_of_the_bus
package tdtd
package game

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import IDMap._
import Enemy._
import scala.collection.mutable.Set

@RunWith(classOf[JUnitRunner])
class AISuite extends FunSuite {

	def createEnemy(r: Float, c: Float, m: GameMap): Enemy = {
		var e = Enemy(FishID, 1)
		e.r = r
		e.c = c
		e.place = m(r,c).get
		e
	}
	def setOcc(m: GameMap, r: Int, c:Int) {
	  m.map(r)(c).occupied = true
	}
	var t4 = new GameMap(9,10,4,4)
	setOcc(t4,0,5)
	setOcc(t4,1,1)
	setOcc(t4,1,2)
	setOcc(t4,1,3)
	setOcc(t4,1,4)
	setOcc(t4,1,7)
	setOcc(t4,2,6)
	setOcc(t4,3,0)
	setOcc(t4,3,1)
	setOcc(t4,3,2)
	setOcc(t4,3,3)
	setOcc(t4,3,4)
	setOcc(t4,3,5)
	setOcc(t4,3,8)
	setOcc(t4,4,7)
	setOcc(t4,5,1)
	setOcc(t4,5,2)
	setOcc(t4,5,3)
	setOcc(t4,5,4)
	setOcc(t4,5,5)
	setOcc(t4,5,6)
	setOcc(t4,7,0)
	setOcc(t4,7,1)
	setOcc(t4,7,2)
	setOcc(t4,7,3)
	setOcc(t4,7,4)
	setOcc(t4,7,5)
	setOcc(t4,7,6)
	setOcc(t4,7,7)
	setOcc(t4,8,7)
	setOcc(t4,9,5)
	t4.dijkstras
	println(t4)
	test("ClosestAI") {
		val e1 = createEnemy(1,1, t4)
		val e2 = createEnemy(1,2, t4)
		val e3 = createEnemy(3,4, t4)
		val e4 = createEnemy(3,5, t4)
		val e5 = createEnemy(2,1, t4)
		val ai = new ClosestAI
		val set1 = Set[Enemy](e1, e2, e3, e4, e5)
		assert(ai.pick(3,3, set1) === e3)
	}
	test("ClosestToGoalAI") {
		val e1 = createEnemy(0,0, t4)
		val e2 = createEnemy(1,0, t4)
		val e3 = createEnemy(1,6, t4)
		val e4 = createEnemy(2,7, t4)
		val e5 = createEnemy(4,2, t4)
		val e6 = createEnemy(3,6, t4)
		val e7 = createEnemy(4,5, t4)
		val set = Set[Enemy](e1, e2, e3, e4, e5, e6, e7)
		val ai = new ClosestToGoalAI
		assert(ai.pick(0,0,set) === e5)
	}
}