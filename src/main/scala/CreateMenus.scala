import org.scalajs.dom
import org.scalajs.dom.{document, html}
import org.scalajs.dom.raw.Event

class CreateMenus(helper: Helper, buttons: Buttons) {

  /*
  ========================
           Helper
  ========================
  */
  def toSolverSettings(): Unit = {
    helper.removeElementByID("main-menu")
    createSettingsMenu(false)
  }

  def toPlaySettings(): Unit = {
    helper.removeElementByID("main-menu")
    createSettingsMenu(true)
  }

  def backToMainMenu(eventfunc: () => Unit): Unit = {
    helper.removeElementByID("mySettingsMenu")
    helper.removeElementByID("mySettingsMenu")
    helper.removeElementByID("playfield")
    helper.removeElementByID("myPuzzleInputGUI")
    eventfunc()
  }

  def backToMenu(toremove: String, eventfunc: () => Unit): Unit = {
    helper.removeElementByID("mySettingsMenu")
    helper.removeElementByID(toremove)
    eventfunc()
  }

  /*
  ========================
      Menu Functions
  ========================
  */

  def createMainMenu(): Unit = {
    helper.appendElement(document.body,"div", "menu", "main-menu")
    var mainmenuElement = helper.getElementByID("main-menu")

    buttons.createButton("Play Nonogram","menu-button",mainmenuElement,true, () => toPlaySettings())
    buttons.createButton("Solver","menu-button",mainmenuElement,true, () => toSolverSettings())
    buttons.createButton("Rules","menu-button",mainmenuElement,true, () => createRulesMenu())
  }

  def createGame(size: String, mode: String): Unit = {
    buttons.createParser(size)
    buttons.setGameMode(mode)
    buttons.gameend = false
    helper.removeElementByID("main-menu")
    buttons.lives = 5
    helper.getElementByID("spacer").setAttribute("class", "spacer50")
    helper.appendElement(document.body,"div", "playfield", "playfield")
    var playfieldElement = helper.getElementByID("playfield")

    var y = buttons.puzzle.getColSegmentSize() + buttons.puzzle.getColSize()
    var x = buttons.puzzle.getRowSegmentSize() + buttons.puzzle.getRowSize()

    buttons.playfield = new CreatePlayField(
      buttons.puzzle.getRowSegmentSize(),
      buttons.puzzle.getColSegmentSize(),
      buttons.puzzle.getRowSize(),
      buttons.puzzle.getColSize(),
    )
    buttons.gameboard = buttons.playfield.initGameBoard()
    playfieldElement.appendChild(buttons.playfield.createPlayTable(
      buttons.puzzle.rowSegments,
      buttons.puzzle.colSegments,
      buttons.buttonFunction))
    val spacer = document.createElement("div")
    spacer.setAttribute("class", "spacer50")
    spacer.id = "spacer1"
    playfieldElement.appendChild(spacer)

    helper.appendElement(playfieldElement, "div", "menu","menu")
    var row1 = helper.getElementByID("menu")
    buttons.createButton("Back","menu-button",row1,true, () => backToMenu("playfield", () => createSettingsMenu(true)))
    buttons.createButton("Check","menu-button",row1,true, () => buttons.checkSolution(true))
  }

  def createSettingsMenu(addGameModeOptions: Boolean): Unit = {
    var selectedMode = !addGameModeOptions
    var selectedSize = false

    val settingsMenu = document.createElement("div")
    settingsMenu.id = "mySettingsMenu"
    document.body.appendChild(settingsMenu)

    val selectionDiv = document.createElement("div")
    val buttonDiv = document.createElement("div")
    val spacer = document.createElement("div")

    selectionDiv.setAttribute("class", "menu")
    buttonDiv.setAttribute("class", "menu")
    buttonDiv.id = "buttons"
    spacer.setAttribute("class", "spacer300")
    spacer.id = "spacer"
    settingsMenu.appendChild(selectionDiv)
    settingsMenu.appendChild(spacer)
    settingsMenu.appendChild(buttonDiv)

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

    val backBtn = buttons.createButton("Back", "menu-button", buttonDiv, true, () => backToMainMenu(() => createMainMenu()))
    backBtn.id = "myBackButton"

    val submitBtn = document.createElement("button")
    submitBtn.id = "mySubmitBtn"
    submitBtn.setAttribute("class", "menu-button")
    submitBtn.textContent = if (addGameModeOptions) "Create Game" else "Input Numbers"
    submitBtn.addEventListener("click", {e: dom.MouseEvent => {
      if (selectedMode && selectedSize) {
        if (addGameModeOptions) {
          modeCaptionDiv.textContent += s" ${modeDDBtn.textContent}"
          helper.removeElementByID(modeDDDiv.id)
          sizeCaptionDiv.textContent += s" ${sizeDDBtn.textContent}"
          helper.removeElementByID(sizeDDDiv.id)
          helper.removeElementByID(buttonDiv.id)
          createGame(sizeDDBtn.textContent, modeDDBtn.textContent)
        } else {
          helper.removeElementByID(settingsMenu.id)

          val size = sizeDDBtn.textContent match {
            case "5 x 5" => 5
            case "10 x 10" => 10
            case "15 x 15" => 15
            case "20 x 20" => 20
            case "25 x 25" => 25
            case _ => throw new Exception(s"Game field size '${sizeDDBtn.textContent}' is not valid")
          }
          createPuzzleInput(size)
        }
      }
    }})

    buttonDiv.appendChild(submitBtn)
  }

  def createRulesMenu(): Unit = {
    helper.removeElementByID("main-menu")

    helper.appendElement(document.body, "div", "rules", "rules")
    var rulesElement = helper.getElementByID("rules")

    helper.appendElement(rulesElement, "div", "h1", "rulestext1")
    var rulesTextElement = helper.getElementByID("rulestext1")
    rulesTextElement.textContent = "The Rules"
    helper.appendElement(rulesElement, "div", "rulestext", "rulestext2")
    rulesTextElement = helper.getElementByID("rulestext2")
    rulesTextElement.textContent = helper.rules1()
    helper.appendElement(rulesElement, "br", "", "")
    helper.appendElement(rulesElement, "div", "rulestext", "rulestext3")
    rulesTextElement = helper.getElementByID("rulestext3")
    rulesTextElement.textContent = helper.rules2()
    helper.appendElement(rulesElement, "br", "", "")

    helper.appendElement(rulesElement, "div", "h1", "rulestext4")
    rulesTextElement = helper.getElementByID("rulestext4")
    rulesTextElement.textContent = "Solution techniques"
    helper.appendElement(rulesElement, "div", "rulestext", "rulestext5")
    rulesTextElement = helper.getElementByID("rulestext5")
    rulesTextElement.textContent = helper.rules3()

    helper.appendElement(rulesElement, "div", "spacer50", "spacer50")
    helper.appendElement(rulesElement, "div", "menu","menu")
    var row1 = helper.getElementByID("menu")
    buttons.createButton("Back","menu-button",row1,true, () => backToMenu("rules",() => createMainMenu()))
    buttons.createButton("Play","menu-button",row1,true, () => backToMenu("rules",() => toPlaySettings()))
  }

  def winloose(s: String): Unit = {
    var playfieldElement = helper.getElementByID("playfield")

    var row1 = helper.getElementByID("spacer1")
    row1.textContent = s

    helper.removeElementByID("menu")
    helper.appendElement(playfieldElement, "div", "menu","menu")
    var row2 = helper.getElementByID("menu")

    buttons.createButton("Back to Main Menu","menu-button",row2,true, () => backToMenu("playfield", () => createMainMenu()))
    buttons.createButton("Play Again","menu-button",row2,true, () => backToMenu("playfield", () => createSettingsMenu(true)))
  }

  def looseMenu(): Unit = {
    winloose("You Lost :( ")
  }

  def winMenu(): Unit = {
   winloose("You Won!")
  }

  def createPuzzleInput(size: Int): Unit = {
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
    for (r <- 0 until size) {
      val resultTableRow = document.createElement("tr")
      resultTable.appendChild(resultTableRow)
      for (c <- 0 until size) {
        val resultTableCell = document.createElement("th")
        resultTableRow.appendChild(resultTableCell)
        val resultTableCellDiv = document.createElement("div")
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
    for (c <- 0 until size) {
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
    for (r <- 0 until size) {
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

    val backBtn = buttons.createButton("Back", "menu-button", puzzleInputDiv, true, () => backToMainMenu(() => createMainMenu()))
    backBtn.id = "myBackButton"

    val solveBtn = buttons.createButton("Solve", "menu-button", puzzleInputDiv, true, () => {
      val rowSegments = List()
      for (r <- 0 until size) {
        rowSegments :+ helper.getElementByID(s"row_$r").textContent.split(" ").toList
      }
      val colSegments = List()
      for (c <- 0 until size) {
        colSegments :+ helper.getElementByID(s"col_$c").textContent.split("<br>").toList
      }
      runSolver(new Puzzle(rowSegments, colSegments))
    })
  }

  def runSolver(puzzle: Puzzle) = {
    //ToDo: run the solver and display solution
  }
}
