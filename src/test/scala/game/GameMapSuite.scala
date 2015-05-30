package com.github.fellowship_of_the_bus
package tdtd
package game

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RegExSuite extends FunSuite {
  var t1 = new GameMap(5,5,2,2)
  var t2 = new GameMap(5,5,2,2)
  var t3 = new GameMap(5,5,2,2)
  setOcc(t1,0,3)
  setOcc(t1,1,1)
  setOcc(t1,1,2)
  setOcc(t1,3,0)
  setOcc(t1,3,1)
  setOcc(t1,3,2)
  setOcc(t1,3,3)
//  var x = t1.placeable(3,4)
//  println(x.toString)
//  var flag = t1.dijkstras
//  test("test map1") {
//    assert( flag == false)
//    assert( t1 === t2 )
//  }
  setOcc(t2,1,2)
  setOcc(t2,1,1)
  setOcc(t2,1,3)
  setOcc(t2,3,1)
  setOcc(t2,3,2)
  setOcc(t2,3,3)
//  flag = t2.dijkstras()
//  test("test map2") {
//    assert( flag == false)
//    assert(t2 === t1) 
//  }
//  flag = t3.dijkstras()
//  test("test map3") {
//    assert( flag == false)
//    assert(t3 === t1)
///  }
  var t4 = new GameMap(9,10,4,4)
  var t5 = new GameMap(9,10,4,4)
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
  test("large map 1 path") {
      assert(t4 === t5)
  }
  def setOcc(m: GameMap, r: Int, c:Int) {
      m.map(r)(c).occupied = true
  }
}


