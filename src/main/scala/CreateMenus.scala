import org.scalajs.dom
import org.scalajs.dom.document

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

  def backToMenu(toremove: String, eventfunc: () => Unit): Unit = {
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
    buttons.createButton("Rules","menu-button",mainmenuElement,true, () => createGame(_,_))
  }

  def createGame(size: String, mode: String): Unit = {
    buttons.createParser(size)
    helper.removeElementByID("main-menu")
    helper.appendElement(document.body,"div", "playfield", "playfield")
    var playfieldElement = helper.getElementByID("playfield")

    var y = buttons.puzzle.getColSegmentSize() + buttons.puzzle.getColSize()
    var x = buttons.puzzle.getRowSegmentSize() + buttons.puzzle.getRowSize()

    //println(puzzle.getColSegmentSize()) // 5
    //println(puzzle.getColSize()) // 1

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
    playfieldElement.appendChild(buttons.playfield.createDebugGameBoard(buttons.gameboard))

    helper.appendElement(playfieldElement, "div", "menu","menu")
    var row1 = helper.getElementByID("menu")
    buttons.createButton("Back","menu-button",row1,true, () => backToMenu("playfield", () => createMainMenu()))
    buttons.createButton("Check","menu-button",row1,true, () => buttons.checkSolution())
  }

  def createSettingsMenu(addGameModeOptions: Boolean) = {
    var selectedMode = !addGameModeOptions
    var selectedSize = false

    // Game mode selection
    val modeCaptionDiv = document.createElement("div")
    val modeDDBtn = document.createElement("button")

    if (addGameModeOptions) {
      modeCaptionDiv.setAttribute("class", "dropdown-caption")
      modeCaptionDiv.textContent = "Game Mode:"
      document.body.appendChild(modeCaptionDiv)

      val modeDDDiv = document.createElement("div")
      modeDDDiv.id = "myModeDropdown"
      modeDDDiv.setAttribute("class", "dropdown")
      document.body.appendChild(modeDDDiv)

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
    val sizeCaptionDiv = document.createElement("div")
    sizeCaptionDiv.setAttribute("class", "dropdown-caption")
    sizeCaptionDiv.textContent = "Game Field Size:"
    document.body.appendChild(sizeCaptionDiv)

    val sizeDDDiv = document.createElement("div")
    sizeDDDiv.id = "mySizeDropdown"
    sizeDDDiv.setAttribute("class", "dropdown")
    document.body.appendChild(sizeDDDiv)

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

    val submitBtn = document.createElement("button")
    submitBtn.id = "mySubmitBtn"
    submitBtn.setAttribute("class", "menu-button")
    submitBtn.textContent = if (addGameModeOptions) "Create Game" else "Input Numbers"
    submitBtn.addEventListener("click", {e: dom.MouseEvent => {
      println("TUT")
      if (selectedMode && selectedSize) {
        if (addGameModeOptions) {
          modeCaptionDiv.textContent += s" ${modeDDBtn.textContent}"
          helper.removeElementByID("myModeDropdown")
          sizeCaptionDiv.textContent += s" ${sizeDDBtn.textContent}"
          helper.removeElementByID("mySizeDropdown")
          helper.removeElementByID("mySubmitBtn")
          createGame(sizeDDBtn.textContent, modeDDBtn.textContent)
        }
        // ToDo: createSolverInput
      }
    }})
    document.body.appendChild(submitBtn)
  }
}