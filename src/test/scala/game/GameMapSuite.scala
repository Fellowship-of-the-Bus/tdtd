package ca.uwaterloo.cs.rschlunt
package tdtd
package game

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RegExSuite extends FunSuite {
  t1 = new GameMap(5,5,2,2)
  t1(0)(3).occupied = true
  t1(1)(1).occupied = true
  t1(1)(2).occupied = true
  t1(3)(0).occupied = true
  t1(3)(1).occupied = true
  t1(3)(2).occupied = true
  t1(3)(4).occupied = true
  ti.dijkstras
  test("test map1") {
    assert( t1 === 5)
  }
  t2 = new GameMap(5,5,2,2)
  t2.(1)(2).occupied = true
  t2.(1)(1).occupied = true
  t2.(1)(3).occupied = true
  t2.(3)(1).occupied = true
  t2.(3)(2).occupied = true
  t2.(3)(3).occupied = true
  test("test map2") {
    assert(t2 === 5) 
  }
}
