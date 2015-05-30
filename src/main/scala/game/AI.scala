package com.github.fellowship_of_the_bus
package tdtd
package game

import scala.collection.mutable.Set
import scala.util.Random
import scala.math._

abstract class AI {
	def pick(r: Float, c: Float, enemies: Set[Enemy]) : Enemy
}

class RandomAI extends AI {
	val rand = new Random()
	override def pick(r: Float, c: Float, enemies: Set[Enemy]) : Enemy = {
		enemies.maxBy(x => rand.nextInt())
	}
}

class ClosestAI extends AI {
	override def pick(r: Float, c: Float, enemies: Set[Enemy]) : Enemy = {
		enemies.minBy(enemy => 	{
				val ydiff = r - enemy.r
				val xdiff = c - enemy.c
				sqrt((xdiff * xdiff) + (ydiff * ydiff))
			}
		)
	}
}