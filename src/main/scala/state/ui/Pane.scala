package com.github.fellowship_of_the_bus
package tdtd
package state
package ui

import lib.slick2d.ui.AbstractPane
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input, Image}

class Pane(x: Float, y: Float, width: Float, height: Float)
          (implicit bg: Color) extends AbstractPane(x,y,width,height) {

  type Game = tdtd.game.Game
}
