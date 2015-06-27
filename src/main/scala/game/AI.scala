package com.github.fellowship_of_the_bus
package tdtd
package game

import scala.collection.mutable.Set
import scala.util.Random
import scala.math._

abstract class AI {
	def pick(r: Float, c: Float, enemies: Set[Enemy]) : Enemy
	def pick(r: Float, c: Float, enemiesU: Set[Enemy],
		enemiesD: Set[Enemy], enemiesL: Set[Enemy], enemiesR: Set[Enemy]) : Enemy = {
		throw new IllegalArgumentException
	}
	override def toString() : String
}

class RandomAI extends AI {
	val rand = new Random()
	override def pick(r: Float, c: Float, enemies: Set[Enemy]) : Enemy = {
		enemies.maxBy(x => rand.nextInt())
	}

	override def toString() : String = "Random"
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

	override def toString() : String = "Closest to Tower"
}

class ClosestToGoalAI extends AI {
	override def pick(r: Float, c: Float, enemies: Set[Enemy]) : Enemy = {
		enemies.minBy(enemy => enemy.place.dist)
	}

	override def toString() : String = "Closest to Goal"
}

class SteamRandomAI extends AI {
	val rand = new Random()
	override def pick(r: Float, c: Float, enemies: Set[Enemy]): Enemy = {
		throw new IllegalArgumentException
	}

	override def pick(r: Float, c: Float, enemiesU: Set[Enemy],
		enemiesD: Set[Enemy], enemiesL: Set[Enemy], enemiesR: Set[Enemy]) : Enemy = {
		val enemies = enemiesU ++ enemiesL ++ enemiesD ++ enemiesR
		enemies.maxBy(x => rand.nextInt())
	}

	override def toString() : String = "Random Direction"
}

class SteamClosestAI extends AI {

	override def pick(r: Float, c: Float, enemies: Set[Enemy]): Enemy = {
		throw new IllegalArgumentException
	}

	override def pick(r: Float, c: Float, enemiesU: Set[Enemy],
		enemiesD: Set[Enemy], enemiesL: Set[Enemy], enemiesR: Set[Enemy]) : Enemy = {
		val enemies = enemiesU ++ enemiesL ++ enemiesD ++ enemiesR;
		enemies.minBy(enemy => 	{
				val ydiff = r - enemy.r
				val xdiff = c - enemy.c
				sqrt((xdiff * xdiff) + (ydiff * ydiff))
			}
		)
	}

	override def toString() : String = "Steam Closest To Tower"
}