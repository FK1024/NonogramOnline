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

  def backToMenu(eventfunc: () => Unit): Unit = {
    helper.removeElementByID("mySettingsMenu")
    helper.removeElementByID("playfield")
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
    //buttons.createButton("Rules","menu-button",mainmenuElement,true, () => createGame(_,_))
  }

  def createGame(size: String, mode: String): Unit = {
    buttons.createParser(size)
    buttons.setGameMode(mode)
    helper.removeElementByID("main-menu")
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
    playfieldElement.appendChild(buttons.playfield.createDebugGameBoard(buttons.gameboard))

    helper.appendElement(playfieldElement, "div", "menu","menu")
    var row1 = helper.getElementByID("menu")
    buttons.createButton("Back","menu-button",row1,true, () => backToMenu( () => createMainMenu()))
    buttons.createButton("Check","menu-button",row1,true, () => buttons.checkSolution())
  }

  def createSettingsMenu(addGameModeOptions: Boolean) = {
    var selectedMode = !addGameModeOptions
    var selectedSize = false

    val settingsMenu = document.createElement("div")
    settingsMenu.id = "mySettingsMenu"
    document.body.appendChild(settingsMenu)

    // Game mode selection
    val modeCaptionDiv = document.createElement("div")
    val modeDDDiv = document.createElement("div")
    val modeDDBtn = document.createElement("button")

    if (addGameModeOptions) {
      val modeSelectionDiv = document.createElement("div")
      settingsMenu.appendChild(modeSelectionDiv)

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
    settingsMenu.appendChild(sizeSelectionDiv)

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

    val backBtn = buttons.createButton("Back", "menu-button", settingsMenu, true, () => backToMenu(() => createMainMenu()))

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
          helper.removeElementByID(submitBtn.id)
          helper.removeElementByID(backBtn.id)
          createGame(sizeDDBtn.textContent, modeDDBtn.textContent)
        }
        // ToDo: createSolverInput
      }
    }})

    settingsMenu.appendChild(submitBtn)
  }

  def winloose(s: String): Unit = {
    helper.removeElementByID("playfield")
    helper.appendElement(document.body, "div", "LooseOrWinMenu", "LooseOrWinMenu")
    var maindiv = helper.getElementByID("LooseOrWinMenu")
    maindiv.textContent = s

    helper.appendElement(maindiv, "div", "LooseOrWinMenuButtons", "LooseOrWinMenuButtons")
    var LooseOrWinMenuButtons = helper.getElementByID("LooseOrWinMenuButtons")

    buttons.createButton("Back to Main Menu","menu-button",LooseOrWinMenuButtons,true, () => backToMenu("LooseOrWinMenu", () => createMainMenu()))
    buttons.createButton("Play Again","menu-button",LooseOrWinMenuButtons,true, () => backToMenu("LooseOrWinMenu", () => createSettingsMenu(true)))
  }

  def looseMenu(): Unit = {
    winloose("You Lost :( ")
  }

  def winMenu(): Unit = {
   winloose("You Won!")
  }
}
