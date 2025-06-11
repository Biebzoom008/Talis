package ch.hevs.gdx2d.hello

import scala.collection.immutable.HashMap

class ScreenManager {
  var screen : Int = 0
  var backgroundChosen : Boolean = false
  var backgroundNumber : Int = 0
  var escape : Boolean = false
  /*
  0 = main menu
  1 = playing
  2 = shop
   */

 var menuScreens : HashMap[Int, String] = HashMap[Int, String](
   1 -> "data/mainMenuScreens/MainMenu.png",
   2 -> "data/mainMenuScreens/MainMenu1.png",
   3 -> "data/mainMenuScreens/MainMenu2.png",
   4 -> "data/mainMenuScreens/MainMenu3.png"
 )
}
