package ch.hevs.gdx2d.alea

import ch.hevs.gdx2d.components.bitmaps.BitmapImage
import ch.hevs.gdx2d.desktop.PortableApplication
import ch.hevs.gdx2d.hello.ScreenManager
import ch.hevs.gdx2d.lib.GdxGraphics
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import java.awt.Desktop
import java.net.URI
import scala.collection.immutable.HashMap
import javax.swing.JOptionPane
import scala.collection.mutable
import scala.util.Random

class DiceGame extends PortableApplication(1920, 1080) {
  // === Paramètres du jeu ===
  val diceSize = 128
  val spacing = 50
  val totalDiceWidth = 5 * diceSize + 4 * spacing
  val diceXStart = (1920 - totalDiceWidth) / 2
  val diceYStart = 400

  val maxRounds = 3
  val maxRollsPerRound = 3

  // === Variables de l'état de jeu ===
  var dice = Array.fill(5)(Random.nextInt(6) + 1)
  var selected = Array.fill(5)(true)
  var currentRound = 1
  var currentRoll = 1
  var totalScore = 0
  var roundScores: List[Int] = List()

  // === Messages temporaires affichés après chaque manche ===
  var lastComboMessage: String = ""
  var lastScoreMessage: String = ""
  var messageDisplayStartTime: Long = 0L
  val messageDisplayDuration: Long = 3000 // en millisecondes

  // === Paramètres des boutons ===
  val buttonWidth = 200
  val buttonHeight = 60
  val buttonSpacing = 50
  val totalButtonWidth = 3 * buttonWidth + 2 * buttonSpacing
  val buttonStartX = (1920 - totalButtonWidth) / 2
  val buttonY = 700

  val buttonX = buttonStartX
  val rulesButtonX = buttonX + buttonWidth + buttonSpacing
  val validateButtonX = rulesButtonX + buttonWidth + buttonSpacing

  // === Initialisation de la fenêtre ===
  override def onInit(): Unit = {
    setTitle("Alea")
  }
  var diceImage : HashMap[Int,String]=HashMap[Int,String](
    1 -> ("data/Dice/d1.png"),
    2 -> ("data/Dice/d2.png"),
    3 -> ("data/Dice/d3.png"),
    4 -> ("data/Dice/d4.png"),
    5 -> ("data/Dice/d5.png"),
    6 -> ("data/Dice/d6.png"),
  )
  //Initialisation du screen manager
   val screenManager : ScreenManager = new ScreenManager



  // === Fonction de rendu graphique appelée chaque frame ===
  override def onGraphicRender(g: GdxGraphics): Unit = {
    if(screenManager.screen == 0){
      if(screenManager.backgroundChosen == false){
        screenManager.backgroundNumber = Random.nextInt(4) + 1
        screenManager.backgroundChosen = true
      }
      g.drawPicture(g.getScreenWidth/2, g.getScreenHeight/2, new BitmapImage(screenManager.menuScreens(screenManager.backgroundNumber)))
      var playButton : BitmapImage = new BitmapImage("data/buttons/PlayButton.png")
      g.drawPicture(g.getScreenWidth/2, g.getScreenHeight/2, playButton )
      if(Gdx.input.isTouched){
        var x = Gdx.input.getX()
        var y = Gdx.input.getY()
        if(x > g.getScreenWidth/2 - 160 & x < g.getScreenWidth/2 + 160 & y > g.getScreenHeight/2 - 80 & y < g.getScreenHeight + 80){
          screenManager.screen += 1
        }
      }
    }
    else if(screenManager.screen == 1){
      if(Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M)){
        screenManager.escape ^= true
      }
    g.clear(Color.FIREBRICK)

      if(screenManager.escape == true){
        g.drawPicture(g.getScreenWidth/2, g.getScreenHeight/2, new BitmapImage("data/mainMenuScreens/EscapeMenu.png"))
        if(Gdx.input.isTouched()){
          var x = Gdx.input.getX()
          var y = Gdx.input.getY()
          println(s"$x and $y")
          if(x > g.getScreenWidth/2 - 16 & x < g.getScreenWidth/2 + 16 & y > g.getScreenHeight/2 - 8 & y < g.getScreenHeight/2 + 10){
            Gdx.app.exit()
          } else if (x > g.getScreenWidth/2 - 48 & x < g.getScreenWidth/2 + 48 & y < g.getScreenHeight/2 - 40 & y > g.getScreenHeight/2 - 64){
            screenManager.escape = false
            screenManager.screen = 0
            //All values are reset to 0
             dice = Array.fill(5)(Random.nextInt(6) + 1)
             selected = Array.fill(5)(true)
             currentRound = 1
             currentRoll = 1
             totalScore = 0
            roundScores = List.empty
            screenManager.backgroundNumber = Random.nextInt(4) + 1
            //Thread sleep so that the play button doesn't get clicked by accident
            Thread.sleep(200)
          } else if(x > g.getScreenWidth/2 - 32 & x < g.getScreenWidth/2 + 32 & y > g.getScreenHeight/2 + 40 & y < g.getScreenHeight/2 + 64){
            openUrl("https://github.com/Biebzoom008/Talis")
          }
        }
      } else if(screenManager.screen == 1){
    // Informations en haut de l'écran
    g.setColor(Color.WHITE)
    g.drawStringCentered(1000, "ALEA")

    g.setColor(Color.GOLD)
    g.drawStringCentered(950, s"Manche : $currentRound / $maxRounds")
    g.drawStringCentered(900, s"Relance : $currentRoll / $maxRollsPerRound")
    g.drawStringCentered(850, s"Score total : $totalScore")

    // Affichage des 5 dés
    for (i <- dice.indices) {
      val x = diceXStart + i * (diceSize + spacing)+diceSize/2
      val y = diceYStart
      if (selected(i)){g.drawPicture(x,y,new BitmapImage(diceImage(dice(i))))}
      else{g.drawAlphaPicture(x,y,0.55f,new BitmapImage(diceImage(dice(i))))}
    }

    // Messages affichés temporairement sous les dés
    val now = System.currentTimeMillis()
    if (now - messageDisplayStartTime <= messageDisplayDuration) {
      g.setColor(Color.YELLOW)
      drawCenteredText(g, 1920 / 2, diceYStart - 90, lastComboMessage)
      drawCenteredText(g, 1920 / 2, diceYStart - 120, lastScoreMessage)
    }

    // Boutons : relancer, règles, valider/rejouer (même emplacement)
    drawButton(g, buttonX+buttonWidth/2, buttonY, buttonWidth, buttonHeight, "Relancer")
    drawButton(g, rulesButtonX+buttonWidth/2, buttonY, buttonWidth, buttonHeight, "Règles")
    val label = if (currentRound > maxRounds) "Rejouer" else "Valider"
    drawButton(g, validateButtonX+buttonWidth/2, buttonY, buttonWidth, buttonHeight, label)}
  }

  // === Dessine un bouton avec texte centré ===
  def drawButton(g: GdxGraphics, x: Int, y: Int, w: Int, h: Int, label: String): Unit = {
    g.setColor(Color.SKY)
    g.drawRectangle(x, y, w, h, 0)
    g.setColor(Color.BLACK)
    drawCenteredText(g, x, y, label)
  }
  }

  // === Texte centré autour du point donné (x, y) ===
  def drawCenteredText(g: GdxGraphics, x: Int, y: Int, text: String): Unit = {
    val charWidth = 4
    val charHeight = 6
    val textWidth = text.length * charWidth
    val textX = x - textWidth
    val textY = y + charHeight
    g.drawString(textX, textY, text)
  }

  // === Gestion des clics ===
  override def onClick(x: Int, y: Int, button: Int): Unit = {
    // Sélection des dés
    for (i <- dice.indices) {
      val dx = diceXStart + i * (diceSize + spacing)
      val dy = diceYStart
      if (x >= dx & x <= dx + diceSize & y >= dy - diceSize/2 & y <= dy + diceSize/2) {
        selected(i) = !selected(i)
      }
    }

    // Relancer les dés sélectionnés
    if (x >= buttonX && x <= buttonX + buttonWidth&& y >= buttonY-buttonHeight/2 && y <= buttonY + buttonHeight/2) {
      if (currentRoll < maxRollsPerRound) {
        rollSelectedDice()
        currentRoll += 1
      }
    }

    // Affichage des règles
    if (x >= rulesButtonX && x <= rulesButtonX + buttonWidth && y >= buttonY-buttonHeight/2 && y <= buttonY + buttonHeight/2) {
      showRules()
    }

    // Bouton Valider ou Rejouer (même emplacement)
    if (x >= validateButtonX && x <= validateButtonX + buttonWidth && y >= buttonY-buttonHeight/2 && y <= buttonY + buttonHeight/2) {
      if (currentRound <= maxRounds) {
        scoreRound()
        if (currentRound <= maxRounds) rollAllDice()
        if (currentRound > maxRounds) showSummary()
      } else {
        resetGame()
      }
    }
  }

  // === Fonction pour relancer les dés sélectionnés ===
  def rollSelectedDice(): Unit = {
    for (i <- dice.indices if selected(i)) {
      dice(i) = Random.nextInt(6) + 1
    }
  }

  // === Relance tous les dés pour une nouvelle manche ===
  def rollAllDice(): Unit = {
    for (i <- dice.indices) {
      dice(i) = Random.nextInt(6) + 1
    }
  }

  // === Calcule le score et stocke le résultat ===
  def scoreRound(): Unit = {
    val scoreThisRound = calculateScore(dice)
    roundScores = roundScores :+ scoreThisRound
    totalScore += scoreThisRound
    selected = Array.fill(5)(true)
    currentRound += 1
    currentRoll = 1

    lastComboMessage = getCombinationName(dice)
    lastScoreMessage = s"Vous avez gagné $scoreThisRound points !"
    messageDisplayStartTime = System.currentTimeMillis()
  }

  //Fonction pour ouvrir un lien
  def openUrl(url : String) : Unit = {
    Desktop.getDesktop.browse(new URI(url))
  }

  // === Identifie le type de combinaison obtenue ===
  def getCombinationName(dice: Array[Int]): String = {
    val counts = dice.groupBy(identity).view.mapValues(_.length).toMap
    val values = counts.keys.toList.sorted

    if (counts.exists(_._2 == 5)) return "Yams !"
    if (counts.exists(_._2 == 4)) return "Carré !"
    if (counts.exists(_._2 == 3) && counts.exists(_._2 == 2)) return "Full House !"
    if ((values == List(1,2,3,4,5)) || (values == List(2,3,4,5,6))) return "Suite !"
    if (counts.exists(_._2 == 3)) return "Brelan !"
    if (counts.count(_._2 == 2) == 2) return "Double Paire !"
    if (counts.exists(_._2 == 2)) return "Paire !"

    "Aucune combinaison"
  }

  // === Réinitialise tous les paramètres pour une nouvelle partie ===
  def resetGame(): Unit = {
    dice = Array.fill(5)(Random.nextInt(6) + 1)
    selected = Array.fill(5)(false)
    currentRound = 1
    currentRoll = 1
    totalScore = 0
    roundScores = List()
    lastComboMessage = ""
    lastScoreMessage = ""
    messageDisplayStartTime = 0L

  }

  // === Affiche les règles du jeu dans une fenêtre ===
  def showRules(): Unit = {
    val message =
      """|Voici les règles de Alea :
         |
         |➔ Yams : 50 points
         |➔ Carré : 40 points
         |➔ Full house : 35 points
         |➔ Suite : 30 points
         |➔ Brelan : 25 points
         |➔ Double paire : 20 points
         |➔ Paire : 10 points
         |
         |Bonne chance !""".stripMargin
    JOptionPane.showMessageDialog(null, message, "Règles", JOptionPane.INFORMATION_MESSAGE)
  }

  // === Affiche un récapitulatif à la fin du jeu ===
  def showSummary(): Unit = {
    val rounds = roundScores.zipWithIndex.map {
      case (score, idx) => s"Manche ${idx + 1} : $score points"
    }.mkString("\n")

    val message = s"Partie terminée !\n\n$rounds\n\nScore total : $totalScore"
    JOptionPane.showMessageDialog(null, message, "Résumé de la partie", JOptionPane.INFORMATION_MESSAGE)
  }

  // === Calcule le score selon les combinaisons obtenues ===
  def calculateScore(dice: Array[Int]): Int = {
    val counts = dice.groupBy(identity).view.mapValues(_.length).toMap
    val values = counts.keys.toList.sorted

    if (counts.exists(_._2 == 5)) return 50
    if (counts.exists(_._2 == 4)) return 40
    if (counts.exists(_._2 == 3) && counts.exists(_._2 == 2)) return 35
    if ((values == List(1, 2, 3, 4, 5)) || (values == List(2, 3, 4, 5, 6))) return 30
    if (counts.exists(_._2 == 3)) return 25
    if (counts.count(_._2 == 2) == 2) return 20
    if (counts.exists(_._2 == 2)) return 10

    0
  }
}

// === Lancement de l'application ===
object DiceGameLauncher {
  def main(args: Array[String]): Unit = {
    new DiceGame()
  }
}
