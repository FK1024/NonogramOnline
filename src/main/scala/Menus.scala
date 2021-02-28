import Enums.{GameMode, State}
import org.scalajs.dom
import org.scalajs.dom.document

import scala.collection.mutable.ListBuffer

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
    val mainMenuElement = DomHelper.appendElement(document.body,"div", "menu", "main-menu")

    Buttons.createButton("Play Nonogram","menu-button", mainMenuElement, () => toPlaySettings())
    Buttons.createButton("Solver","menu-button", mainMenuElement, () => toSolverSettings())
    Buttons.createButton("Rules","menu-button", mainMenuElement, () => createRulesMenu())
  }

  def createGame(): Unit = {
    gameContext.puzzle = getRandomPuzzle(gameContext.size)
    gameContext.solution = Solver.solve(gameContext.puzzle)
    if (gameContext.mode == GameMode.FiveLives) addHearts()
    gameContext.gameOver = false
    gameContext.lives = 5

    DomHelper.removeElementByID("main-menu")
    document.getElementById("spacer").setAttribute("class", "spacer50")
    val playFieldElement = DomHelper.appendElement(document.body, "div", "playfield", "playfield")

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

    val row1 = DomHelper.appendElement(playFieldElement, "div", "menu", "menu")
    Buttons.createButton("Back", "menu-button", row1, () => backToMenu("playfield", () => createSettingsMenu(true)))
    Buttons.createButton("Check", "menu-button", row1, () => Buttons.checkSolution(true))
  }

  // ToDo: load random level instead
  private def getRandomPuzzle(size: Int) = {
    size match {
      case 5 => Parser.parseDefinition(Heart.puzzle)
      case 10 => Parser.parseDefinition(MrKrabs.puzzle)
      case 15 => Parser.parseDefinition(House.puzzle)
      case 20 => Parser.parseDefinition(SailingShip.puzzle)
      case 25 => throw new NotImplementedError()
    }
  }

  private def addHearts(): Unit = {
    var spacer = document.getElementById("spacer")
    DomHelper.removeElementByID("lives")
    var lives = DomHelper.appendElement(spacer, "div", "lives","lives")
    val hearttext = DomHelper.appendElement(lives, "div", "text","hearttext")
    hearttext.textContent = "Lives: "

    for(i <- 0 to 4) {
      DomHelper.appendElement(lives, "div", "heart","heart" + i)
    }
  }

  def createSettingsMenu(addGameModeOptions: Boolean): Unit = {
    var selectedMode = !addGameModeOptions
    var selectedSize = false

    val settingsMenuDiv = document.createElement("div")
    settingsMenuDiv.id = "mySettingsMenu"
    document.body.appendChild(settingsMenuDiv)

    val selectionDiv = document.createElement("div")
    val buttonDiv = document.createElement("div")
    val spacer = document.createElement("div")

    selectionDiv.setAttribute("class", "menu")
    buttonDiv.setAttribute("class", "menu")
    buttonDiv.id = "buttons"
    spacer.setAttribute("class", "spacer300")
    spacer.id = "spacer"
    settingsMenuDiv.appendChild(selectionDiv)
    settingsMenuDiv.appendChild(spacer)
    settingsMenuDiv.appendChild(buttonDiv)

    // Game mode selection
    val modeCaptionDiv = document.createElement("div")
    val modeDDDiv = document.createElement("div")
    val modeDDBtn = document.createElement("button")

    if (addGameModeOptions) {
      val modeSelectionDiv = document.createElement("div")
      selectionDiv.appendChild(modeSelectionDiv)

      modeCaptionDiv.setAttribute("class", "dropdown-caption")
      modeCaptionDiv.textContent = "Game Mode:"
      modeSelectionDiv.appendChild(modeCaptionDiv)

      modeDDDiv.id = "myModeDropdown"
      modeDDDiv.setAttribute("class", "dropdown")
      modeSelectionDiv.appendChild(modeDDDiv)

      modeDDBtn.setAttribute("class", "dropdown-button")
      modeDDBtn.textContent = "Select Mode ..."
      modeDDDiv.appendChild(modeDDBtn)

      val modeContentDiv = document.createElement("div")
      modeContentDiv.setAttribute("class", "dropdown-content")
      modeDDDiv.appendChild(modeContentDiv)

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
    val sizeSelectionDiv = document.createElement("div")
    selectionDiv.appendChild(sizeSelectionDiv)

    val sizeCaptionDiv = document.createElement("div")
    sizeCaptionDiv.setAttribute("class", "dropdown-caption")
    sizeCaptionDiv.textContent = "Game Field Size:"
    sizeSelectionDiv.appendChild(sizeCaptionDiv)

    val sizeDDDiv = document.createElement("div")
    sizeDDDiv.id = "mySizeDropdown"
    sizeDDDiv.setAttribute("class", "dropdown")
    sizeSelectionDiv.appendChild(sizeDDDiv)

    val sizeDDBtn = document.createElement("button")
    sizeDDBtn.setAttribute("class", "dropdown-button")
    sizeDDBtn.textContent = "Select Size ..."
    sizeDDDiv.appendChild(sizeDDBtn)

    val sizeContentDiv = document.createElement("div")
    sizeContentDiv.setAttribute("class", "dropdown-content")
    sizeDDDiv.appendChild(sizeContentDiv)

    for (s <- 5 to 25 by 5) {
      val sizeBtn = document.createElement("button")
      sizeBtn.setAttribute("class", "dropdown-element-button")
      sizeBtn.textContent = s"$s x $s"
      sizeBtn.addEventListener("click", {e: dom.MouseEvent => {
        sizeDDBtn.textContent = sizeBtn.textContent
        selectedSize = true
      }})
      sizeContentDiv.appendChild(sizeBtn)
    }

    val backBtn = Buttons.createButton("Back", "menu-button", buttonDiv, () => backToMainMenu(() => createMainMenu()))
    backBtn.id = "myBackButton"

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
          case "25 x 25" => 25
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
          createGame()
        } else {
          DomHelper.removeElementByID(settingsMenuDiv.id)
          createPuzzleInput()
        }
      }
    }})

    buttonDiv.appendChild(submitBtn)
  }

  def rules1(): String = {
    "Your aim in these puzzles is to colour the whole grid into black and white squares. " +
      "Leaving one empty, is equal to marking it white. " +
      "Beside each row of the grid are listed the lengths of the runs of black squares on that row. " +
      "Above each column are listed the lengths of the runs of black squares in that column. " +
      "These numbers tell you the runs of black squares in that row/column. So, if you see '10 1', " +
      "that tells you that there will be a run of exactly 10 black squares, followed by one or more white square, followed by a single black square. " +
      "There may be more white squares before/after this sequence. "
  }

  def rules2(): String = {
    "Left click on a square to make it black. Right click to mark it white. Click and drag to mark more than one square"
  }

  def rules3(): String = {
    "To solve a puzzle, one needs to determine which cells will be boxes and which will be empty. " +
      "Determining which cells are to be left empty is as important as determining which to fill. " +
      "Later in the solving process, the spaces help determine where a clue may spread. " +
      "Solvers usually use a dot or a cross to mark cells they are certain are spaces. " +
      "It is also important never to guess. Only cells that can be determined by logic should be filled. " +
      "If guessing, a single error can spread over the entire field and completely ruin the solution."
  }

  def createRulesMenu(): Unit = {
    DomHelper.removeElementByID("main-menu")

    val rulesElement = DomHelper.appendElement(document.body, "div", "rules", "rules")

    var rulesTextElement = DomHelper.appendElement(rulesElement, "div", "h1", "rulestext1")
    rulesTextElement.textContent = "The Rules"
    rulesTextElement = DomHelper.appendElement(rulesElement, "div", "rulestext", "rulestext2")
    rulesTextElement.textContent = rules1()
    DomHelper.appendElement(rulesElement, "br", "", "")
    rulesTextElement = DomHelper.appendElement(rulesElement, "div", "rulestext", "rulestext3")
    rulesTextElement.textContent = rules2()
    DomHelper.appendElement(rulesElement, "br", "", "")

    rulesTextElement = DomHelper.appendElement(rulesElement, "div", "h1", "rulestext4")
    rulesTextElement.textContent = "Solution techniques"
    rulesTextElement = DomHelper.appendElement(rulesElement, "div", "rulestext", "rulestext5")
    rulesTextElement.textContent = rules3()

    DomHelper.appendElement(rulesElement, "div", "spacer50", "spacer50")
    val row1 = DomHelper.appendElement(rulesElement, "div", "menu", "menu")
    Buttons.createButton("Back", "menu-button", row1, () => backToMenu("rules", () => createMainMenu()))
    Buttons.createButton("Play", "menu-button", row1, () => backToMenu("rules", () => toPlaySettings()))
  }

  def playagain(): Unit = {
    DomHelper.removeElementByID("playfield")
    createGame()
  }

  def winloose(s: String): Unit = {
    var playfieldElement = document.getElementById("playfield")

    var row1 = document.getElementById("spacer1")
    row1.textContent = s

    DomHelper.removeElementByID("menu")
    val row2 = DomHelper.appendElement(playfieldElement, "div", "menu", "menu")

    Buttons.createButton("Back to Main Menu", "menu-button", row2, () => backToMenu("playfield", () => createMainMenu()))
    Buttons.createButton("Play Again", "menu-button", row2, () => playagain())
  }

  def looseMenu(): Unit = {
    winloose("You Lost :( ")
  }

  def winMenu(): Unit = {
   winloose("You Won!")
  }

  def createPuzzleInput(): Unit = {
    val puzzleInputDiv = document.createElement("div")
    puzzleInputDiv.id = "myPuzzleInputGUI"
    document.body.appendChild(puzzleInputDiv)

    // main table
    val outerTable = document.createElement("table")
    puzzleInputDiv.appendChild(outerTable)

    val firstRow = document.createElement("tr")
    outerTable.appendChild(firstRow)

    val firstCell = document.createElement("th")
    firstRow.appendChild(firstCell)

    val firstCellDiv = document.createElement("div")
    firstCell.appendChild(firstCellDiv)

    val secondCell = document.createElement("th")
    firstRow.appendChild(secondCell)

    val secondCellDiv = document.createElement("div")
    secondCell.appendChild(secondCellDiv)

    val secondRow = document.createElement("tr")
    outerTable.appendChild(secondRow)

    val thirdCell = document.createElement("th")
    secondRow.appendChild(thirdCell)

    val thirdCellDiv = document.createElement("div")
    thirdCell.appendChild(thirdCellDiv)

    val fourthCell = document.createElement("th")
    secondRow.appendChild(fourthCell)

    val fourthCellDiv = document.createElement("div")
//    fourthCellDiv.setAttribute("class", "table-size")
    fourthCell.appendChild(fourthCellDiv)

    // result table
    val resultTable = document.createElement("table")
    resultTable.setAttribute("class","styled-table")
    for (r <- 0 until gameContext.size) {
      val resultTableRow = document.createElement("tr")
      resultTable.appendChild(resultTableRow)
      for (c <- 0 until gameContext.size) {
        val resultTableCell = document.createElement("th")
        resultTableCell.setAttribute("class", "th1")
        resultTableRow.appendChild(resultTableCell)
        val resultTableCellDiv = document.createElement("div")
        resultTableCellDiv.id = s"$r|$c"
        resultTableCellDiv.setAttribute("class", "table-size")
        resultTableCell.appendChild(resultTableCellDiv)
      }
    }
    fourthCellDiv.appendChild(resultTable)

    // column segments input table
    val colSegsTable = document.createElement("table")
    colSegsTable.setAttribute("class","styled-table")
    val colSegsTableRow = document.createElement("tr")
    colSegsTable.appendChild(colSegsTableRow)
    for (c <- 0 until gameContext.size) {
      val colSegsTableCell = document.createElement("th")
      colSegsTableCell.setAttribute("class", "column-segments")
      colSegsTableRow.appendChild(colSegsTableCell)
      val colSegsTableCellDiv = document.createElement("div")
      colSegsTableCellDiv.id = s"col_$c"
      colSegsTableCellDiv.setAttribute("class", "column-segments-input")
      colSegsTableCellDiv.setAttribute("contenteditable", "true")
      colSegsTableCell.appendChild(colSegsTableCellDiv)
    }
    secondCellDiv.appendChild(colSegsTable)

    // row segments input table
    val rowSegsTable = document.createElement("table")
    rowSegsTable.setAttribute("class", "styled-table")
    for (r <- 0 until gameContext.size) {
      val rowSegsTableRow = document.createElement("tr")
      rowSegsTable.appendChild(rowSegsTableRow)
      val rowSegsTableCell = document.createElement("th")
      rowSegsTableCell.setAttribute("class", "row-segments")
      rowSegsTableRow.appendChild(rowSegsTableCell)
      val rowSegsTableCellDiv = document.createElement("div")
      rowSegsTableCellDiv.id = s"row_$r"
      rowSegsTableCellDiv.setAttribute("class", "row-segments-input")
      rowSegsTableCellDiv.setAttribute("contenteditable", "true")
      rowSegsTableCell.appendChild(rowSegsTableCellDiv)
      rowSegsTableCellDiv.addEventListener("keydown", {e: dom.KeyboardEvent =>
        if (e.keyCode == 13) e.preventDefault()
      })
    }
    thirdCellDiv.appendChild(rowSegsTable)

    val backBtn = Buttons.createButton("Back", "menu-button", puzzleInputDiv, () => backToMainMenu(() => createMainMenu()))
    backBtn.id = "myBackButton"

    val solveBtn = Buttons.createButton("Solve", "menu-button", puzzleInputDiv, () => {
      val rowSegments = new ListBuffer[List[Int]]()
      val colSegments = new ListBuffer[List[Int]]()
      for (i <- 0 until gameContext.size) {
        rowSegments.append(document.getElementById(s"row_$i").textContent.split(" ").toList.map(_.toInt))
        colSegments.append(document.getElementById(s"col_$i").innerHTML.split("<br>").toList.map(_.toInt))
      }
      runSolver(new Puzzle(rowSegments.toList, colSegments.toList))
    })
    solveBtn.id = "mySolveButton"
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
