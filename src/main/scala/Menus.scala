import Enums.{GameMode, NodeType, State}
import org.scalajs.dom
import org.scalajs.dom.document

import scala.collection.mutable.ListBuffer
import scala.util.Random

object Menus {
  var gameContext = new GameContext()

  /*
  ========================
    Navigation Functions
  ========================
  */
  def toSolverSettings(): Unit = {
    DomHelper.removeElementByID("main-menu")
    createSettingsMenu(false)
  }

  def toPlaySettings(): Unit = {
    DomHelper.removeElementByID("main-menu")
    createSettingsMenu(true)
  }

  def backToMainMenu(eventfunc: () => Unit): Unit = {
    DomHelper.removeElementByID("mySettingsMenu")
    DomHelper.removeElementByID("playfield")
    DomHelper.removeElementByID("myPuzzleInputGUI")
    eventfunc()
  }

  def backToMenu(toremove: String, eventfunc: () => Unit): Unit = {
    DomHelper.removeElementByID("mySettingsMenu")
    DomHelper.removeElementByID(toremove)
    eventfunc()
  }

  /*
  ========================
      Menu Functions
  ========================
  */

  def createMainMenu(): Unit = {
    val mainMenuElement = DomHelper.appendElement(NodeType.Div, document.body, "menu", "main-menu")

    DomHelper.createButton(mainMenuElement, "menu-button", "Play Nonogram", () => toPlaySettings())
    DomHelper.createButton(mainMenuElement, "menu-button", "Solver", () => toSolverSettings())
    DomHelper.createButton(mainMenuElement, "menu-button", "Rules", () => createRulesMenu())
  }

  def createGame(playagain: Boolean): Unit = {
    Buttons.last_x = -1
    Buttons.last_y = -1
    if (!playagain) gameContext.puzzle = getRandomPuzzle(gameContext.size)
    gameContext.solution = Solver.solve(gameContext.puzzle)
    if (gameContext.mode == GameMode.FiveLives) addHearts()
    gameContext.gameOver = false
    gameContext.lives = 5

    DomHelper.removeElementByID("main-menu")
    document.getElementById("spacer").setAttribute("class", "spacer50")
    val playFieldElement = DomHelper.appendElement(NodeType.Div, document.body, "playfield", "playfield")

    Buttons.playfield = new PlayField(
      gameContext.puzzle.getRowSegmentSize(),
      gameContext.puzzle.getColSegmentSize(),
      gameContext.puzzle.getRowSize(),
      gameContext.puzzle.getColSize(),
    )
    Buttons.gameboard = Buttons.playfield.initGameBoard()
    playFieldElement.appendChild(Buttons.playfield.createPlayTable(
      gameContext.puzzle.rowSegments,
      gameContext.puzzle.colSegments,
      Buttons.buttonFunction))
    val spacer = document.createElement("div")
    spacer.setAttribute("class", "spacer50")
    spacer.id = "spacer1"
    playFieldElement.appendChild(spacer)

    val row1 = DomHelper.appendElement(NodeType.Div, playFieldElement, "menu", "menu")
    DomHelper.createButton(row1, "menu-button", "Back", () => backToMenu("playfield", () => createSettingsMenu(true)))
    if (gameContext.mode != GameMode.FiveLives) DomHelper.createButton(row1, "menu-button", "Check", () => Buttons.checkSolution(true))
    else DomHelper.createButton(row1, "menu-button", "Back to Main Menu", () => backToMainMenu(() => createMainMenu()))
  }

  private def getRandomPuzzle(size: Int): Puzzle = {
    val puzzleDefinition = size match {
      case 5 => PuzzleList.size5(Random.nextInt(PuzzleList.size5.length))
      case 10 => PuzzleList.size10(Random.nextInt(PuzzleList.size10.length))
      case 15 => PuzzleList.size15(Random.nextInt(PuzzleList.size15.length))
      case 20 => PuzzleList.size20(Random.nextInt(PuzzleList.size20.length))
      case _ => throw new Exception(s"there exist no puzzles for size $size")
    }

    return Parser.parseDefinition(puzzleDefinition)
  }

  private def addHearts(): Unit = {
    val spacer = document.getElementById("spacer")
    DomHelper.removeElementByID("lives")
    val lives = DomHelper.appendElement(NodeType.Div, spacer, "lives", "lives")
    DomHelper.appendElement(NodeType.Div, lives, "text", "hearttext", "Lives: ")

    for(i <- 0 to 4) {
      DomHelper.appendElement(NodeType.Div, lives, "heart", "heart" + i)
    }
  }

  def createSettingsMenu(addGameModeOptions: Boolean): Unit = {
    var selectedMode = !addGameModeOptions
    var selectedSize = false

    val settingsMenuDiv = DomHelper.appendElement(NodeType.Div, document.body, id = "mySettingsMenu")
    val selectionDiv = DomHelper.appendElement(NodeType.Div, settingsMenuDiv, "menu")
    DomHelper.appendElement(NodeType.Div, settingsMenuDiv, "spacer300", "spacer")
    val buttonDiv = DomHelper.appendElement(NodeType.Div, settingsMenuDiv, "menu", "buttons")

    // Game mode selection
    val modeCaptionDiv = document.createElement("div")
    val modeDDDiv = document.createElement("div")
    val modeDDBtn = document.createElement("button")

    if (addGameModeOptions) {
      val modeSelectionDiv = DomHelper.appendElement(NodeType.Div, selectionDiv)

      modeCaptionDiv.setAttribute("class", "dropdown-caption")
      modeCaptionDiv.textContent = "Game Mode:"
      modeSelectionDiv.appendChild(modeCaptionDiv)

      modeDDDiv.id = "myModeDropdown"
      modeDDDiv.setAttribute("class", "dropdown")
      modeSelectionDiv.appendChild(modeDDDiv)

      modeDDBtn.setAttribute("class", "dropdown-button")
      modeDDBtn.textContent = "Select Mode ..."
      modeDDDiv.appendChild(modeDDBtn)

      val modeContentDiv = DomHelper.appendElement(NodeType.Div, modeDDDiv, "dropdown-content")

      val fiveLivesBtn = document.createElement("button")
      fiveLivesBtn.setAttribute("class", "dropdown-element-button")
      fiveLivesBtn.textContent = "5 Lives Mode"
      fiveLivesBtn.addEventListener("click", {e: dom.MouseEvent => {
        modeDDBtn.textContent = fiveLivesBtn.textContent
        selectedMode = true
      }})
      modeContentDiv.appendChild(fiveLivesBtn)

      val hardcoreModeBtn = document.createElement("button")
      hardcoreModeBtn.setAttribute("class", "dropdown-element-button")
      hardcoreModeBtn.textContent = "Hardcore Mode"
      hardcoreModeBtn.addEventListener("click", {e: dom.MouseEvent => {
        modeDDBtn.textContent = hardcoreModeBtn.textContent
        selectedMode = true
      }})
      modeContentDiv.appendChild(hardcoreModeBtn)
    }

    // Size selection
    val sizeSelectionDiv = DomHelper.appendElement(NodeType.Div, selectionDiv)
    val sizeCaptionDiv = DomHelper.appendElement(NodeType.Div, sizeSelectionDiv, "dropdown-caption", "Game Field Size:", "Game Field Size:")
    val sizeDDDiv = DomHelper.appendElement(NodeType.Div, sizeSelectionDiv, "dropdown", "mySizeDropdown")
    val sizeDDBtn = DomHelper.createButton(sizeDDDiv, "dropdown-button", "Select Size ...")
    val sizeContentDiv = DomHelper.appendElement(NodeType.Div, sizeDDDiv, "dropdown-content")

    for (s <- 5 to 20 by 5) {
      val sizeBtn = document.createElement("button")
      sizeBtn.setAttribute("class", "dropdown-element-button")
      sizeBtn.textContent = s"$s x $s"
      sizeBtn.addEventListener("click", {e: dom.MouseEvent => {
        sizeDDBtn.textContent = sizeBtn.textContent
        selectedSize = true
      }})
      sizeContentDiv.appendChild(sizeBtn)
    }

    DomHelper.createButton(buttonDiv, "menu-button", "Back", () => backToMainMenu(() => createMainMenu()), "myBackButton")

    val submitBtn = document.createElement("button")
    submitBtn.id = "mySubmitBtn"
    submitBtn.setAttribute("class", "menu-button")
    submitBtn.textContent = if (addGameModeOptions) "Create Game" else "Input Numbers"
    submitBtn.addEventListener("click", {e: dom.MouseEvent => {
      if (selectedMode && selectedSize) {
        // parse size
        gameContext.size = sizeDDBtn.textContent match {
          case "5 x 5" => 5
          case "10 x 10" => 10
          case "15 x 15" => 15
          case "20 x 20" => 20
          case _ => throw new Exception(s"Game field size '${sizeDDBtn.textContent}' is not valid")
        }
        if (addGameModeOptions) {
          // parse mode
          gameContext.mode = modeDDBtn.textContent match {
            case "5 Lives Mode" => GameMode.FiveLives
            case "Hardcore Mode" => GameMode.Default
            case _ => throw new Exception(s"Game mode '${modeDDBtn.textContent}' is not valid'")
          }
          // update captions and remove dropdowns
          modeCaptionDiv.textContent += s" ${modeDDBtn.textContent}"
          DomHelper.removeElementByID(modeDDDiv.id)
          sizeCaptionDiv.textContent += s" ${sizeDDBtn.textContent}"
          DomHelper.removeElementByID(sizeDDDiv.id)
          DomHelper.removeElementByID(buttonDiv.id)
          createGame(false)
        } else {
          DomHelper.removeElementByID(settingsMenuDiv.id)
          createPuzzleInput()
        }
      }
    }})

    buttonDiv.appendChild(submitBtn)
  }

  def createRulesMenu(): Unit = {
    DomHelper.removeElementByID("main-menu")

    val rulesElement = DomHelper.appendElement(NodeType.Div, document.body, "rules", "rules")

    var rulesTextElement = DomHelper.appendElement(NodeType.Div, rulesElement, "h1", "rulestext1", "The Rules")
    rulesTextElement = DomHelper.appendElement(NodeType.Div, rulesElement, "rulestext", "rulestext2", RulesText.rules1)
    DomHelper.appendElement(NodeType.Br, rulesElement)
    rulesTextElement = DomHelper.appendElement(NodeType.Div, rulesElement, "rulestext", "rulestext3", RulesText.rules2)
    DomHelper.appendElement(NodeType.Br, rulesElement)

    rulesTextElement = DomHelper.appendElement(NodeType.Div, rulesElement, "h1", "rulestext4", "Solution techniques")
    rulesTextElement = DomHelper.appendElement(NodeType.Div, rulesElement, "rulestext", "rulestext5", RulesText.rules3)

    DomHelper.appendElement(NodeType.Div, rulesElement, "spacer50", "spacer50")
    val row1 = DomHelper.appendElement(NodeType.Div, rulesElement, "menu", "menu")
    DomHelper.createButton(row1, "menu-button", "Back", () => backToMenu("rules", () => createMainMenu()))
    DomHelper.createButton(row1, "menu-button", "Play", () => backToMenu("rules", () => toPlaySettings()))
  }

  def playAgain(): Unit = {
    DomHelper.removeElementByID("playfield")
    createGame(true)
  }

  def winLoose(s: String): Unit = {
    val playfieldElement = document.getElementById("playfield")

    val row1 = document.getElementById("spacer1")
    row1.textContent = s

    DomHelper.removeElementByID("menu")
    val row2 = DomHelper.appendElement(NodeType.Div, playfieldElement, "menu", "menu")

    DomHelper.createButton(row2, "menu-button", "Back to Main Menu", () => backToMenu("playfield", () => createMainMenu()))
    DomHelper.createButton(row2, "menu-button", "Play Again", () => playAgain())
  }

  def looseMenu(): Unit = {
    winLoose("You Lost :( ")
  }

  def winMenu(): Unit = {
   winLoose("You Won!")
  }

  def createPuzzleInput(): Unit = {
    val puzzleInputDiv = DomHelper.appendElement(NodeType.Div, document.body, id = "myPuzzleInputGUI")

    // main table
    val outerTable = DomHelper.appendElement(NodeType.Table, puzzleInputDiv)

    val firstRow = DomHelper.appendElement(NodeType.Tr, outerTable)
    DomHelper.appendElement(NodeType.Th, firstRow)

    val secondCell = DomHelper.appendElement(NodeType.Th, firstRow)
    val secondCellDiv = DomHelper.appendElement(NodeType.Div, secondCell)

    val secondRow = DomHelper.appendElement(NodeType.Tr, outerTable)

    val thirdCell = DomHelper.appendElement(NodeType.Th, secondRow)
    val thirdCellDiv = DomHelper.appendElement(NodeType.Div, thirdCell)

    val fourthCell = DomHelper.appendElement(NodeType.Th, secondRow)
    val fourthCellDiv = DomHelper.appendElement(NodeType.Div, fourthCell)

    // result table
    val resultTable = DomHelper.appendElement(NodeType.Table, fourthCellDiv, "styled-table")
    for (r <- 0 until gameContext.size) {
      val resultTableRow = DomHelper.appendElement(NodeType.Tr, resultTable)
      for (c <- 0 until gameContext.size) {
        val resultTableCell = DomHelper.appendElement(NodeType.Th, resultTableRow, "th1")
        DomHelper.appendElement(NodeType.Div, resultTableCell, "table-size", s"$r|$c")
      }
    }

    // column segments input table
    val colSegsTable = DomHelper.appendElement(NodeType.Table, secondCellDiv, "styled-table")
    val colSegsTableRow = DomHelper.appendElement(NodeType.Tr, colSegsTable)
    for (c <- 0 until gameContext.size) {
      val colSegsTableCell = DomHelper.appendElement(NodeType.Th, colSegsTableRow, "column-segments")
      val colSegsTableCellDiv = DomHelper.appendElement(NodeType.Div, colSegsTableCell, "column-segments-input", s"col_$c")
      colSegsTableCellDiv.setAttribute("contenteditable", "true")
    }

    // row segments input table
    val rowSegsTable = DomHelper.appendElement(NodeType.Table, thirdCellDiv, "styled-table")
    for (r <- 0 until gameContext.size) {
      val rowSegsTableRow = DomHelper.appendElement(NodeType.Tr, rowSegsTable)
      val rowSegsTableCell = DomHelper.appendElement(NodeType.Th, rowSegsTableRow, "row-segments")
      val rowSegsTableCellDiv = DomHelper.appendElement(NodeType.Div, rowSegsTableCell, "row-segments-input", s"row_$r")
      rowSegsTableCellDiv.setAttribute("contenteditable", "true")
      rowSegsTableCellDiv.addEventListener("keydown", {e: dom.KeyboardEvent =>
        if (e.keyCode == 13) e.preventDefault()
      })
    }


    DomHelper.appendElement(NodeType.Div, puzzleInputDiv, "spacer50", "spacer")
    val row1 = DomHelper.appendElement(NodeType.Div, puzzleInputDiv, "menu", "menu")
    DomHelper.createButton(row1, "menu-button", "Back", () => backToMenu("myPuzzleInputGUI", () => toSolverSettings()), "myBackButton")
    DomHelper.createButton(row1, "menu-button", "Back to Main Menu", () => backToMainMenu(() => createMainMenu()))

    DomHelper.createButton(row1, "menu-button", "Solve", () => {
          val rowSegments = new ListBuffer[List[Int]]()
          val colSegments = new ListBuffer[List[Int]]()
          for (i <- 0 until gameContext.size) {
            rowSegments.append(document.getElementById(s"row_$i").textContent.split(" ").toList.map(_.toInt))
            colSegments.append(document.getElementById(s"col_$i").innerHTML.split("<br>").toList.map(_.toInt))
          }
          runSolver(new Puzzle(rowSegments.toList, colSegments.toList))
        }, "mySolveButton")
  }

  def runSolver(puzzle: Puzzle): Unit = {
    val solution = Solver.solve(puzzle)

    for (r <- puzzle.rowSegments.indices) {
      for (c <- puzzle.colSegments.indices) {
        val cellDiv = document.getElementById(s"$r|$c")
        solution(r)(c) match {
          case State.Blank => cellDiv.setAttribute("class", "table-button-pressed2")
          case State.Set => cellDiv.setAttribute("class", "table-button-pressed1")
        }
      }
    }
  }
}
