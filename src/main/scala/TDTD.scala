package com.github.fellowship_of_the_bus
package tdtd
import java.util.logging.{Level, Logger}
import org.newdawn.slick.{AppGameContainer, GameContainer, Graphics, SlickException,Color, Input, Image}
import org.newdawn.slick.state.{BasicGameState, StateBasedGame}

import state._
import game._
import lib.util.Native
import lib.game.GameConfig
import lib.slick2d.game.SlickGameConfig

class TDTD(gamename: String) extends StateBasedGame(gamename) {
  def initStatesList(gc: GameContainer) = {
    gc.setShowFPS(false)
    addState(Menu)
    addState(GameUI)
    addState(Options)
    addState(Encyclopedia)
  }
}

object TDTD extends App {
  def makeImg(loc: String) = new Image(loc)

  GameConfig.Width = 1280
  GameConfig.Height = 720
  GameConfig.FrameRate = 60

  try {
    import GameConfig._
    Native.loadLibraryFromJar()
    val appgc = new AppGameContainer(new TDTD("Two-Dimensional Tower Defense"))
    SlickGameConfig.graphics = appgc.getGraphics
    appgc.setDisplayMode(Width, Height, false)
    appgc.setTargetFrameRate(FrameRate)
    appgc.setVSync(true)
    appgc.start()
  } catch {
    case ex: SlickException => Logger.getLogger(TDTD.getClass.getName()).log(Level.SEVERE, null, ex)
    case t: Throwable =>
      println("Library path is: " + System.getProperty("java.library.path"))
      t.printStackTrace
  }
}
