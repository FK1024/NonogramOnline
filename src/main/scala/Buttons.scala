import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element


class Buttons(helper: Helper) {
  var puzzle:Puzzle = null
  var solver:Solver = null
  var menureference:CreateMenus = null
  var playfield:CreatePlayField = null

  var gamemode = Gamemode.Default
  var gameboard:Array[Array[Int]] = Array()
  var last_x = -1
  var last_y = -1
  var lives = 5

  var parser = new Parser()

  def createButton(text: String, classname: String, targetNode: dom.Node, event: Boolean, eventfunc: () => Unit): Element = {
    val button = document.createElement("button")
    button.textContent = text
    button.setAttribute("class",classname)

    if (event) {
      button.addEventListener("click", {e: dom.MouseEvent =>
        eventfunc()
      })
    }
    targetNode.appendChild(button)
    return button
  }

  def buttonHover(x: Int, y: Int, style: String): Unit = {
    var size_x = puzzle.rowSegments(y-1).length
    var size_y = puzzle.colSegments(x-1).length

    for(y1 <- puzzle.getColSize() until puzzle.getColSize()-size_y by -1) {
      helper.getElementByID("r"+x+"|"+y1).setAttribute("class", style)
    }
    for(x1 <- puzzle.getRowSize() until puzzle.getRowSize()-size_x by -1) {
      helper.getElementByID("c"+x1+"|"+y).setAttribute("class", style)
    }
  }

  def buttonFunction(x: Int, y: Int, x1: Int, y1: Int, drag: Boolean, hover: Boolean, mode: String): Unit = {
    if (hover) {
      if(last_y >= 0) buttonHover(last_x, last_y, "table-size")
      buttonHover(x,y, "table-size-hover")
      last_x = x
      last_y = y
    }
    else buttonGameBoard(x, y, x1, y1, drag, mode)
  }

  def buttonGameBoard(x: Int, y: Int, x1: Int, y1: Int, drag: Boolean, mode: String): Unit = {
    if(mode == "right") {
      if(drag) {
        replaceInGameBoard(-3,0,"table-button")
        replaceInGameBoard(-2,2,"table-button-pressed2")
        replaceInGameBoard(-1,1,"table-button-pressed1")
        drawDrag(x,y,x1,y1,"table-button-pressed1")
      } else {
        replaceInGameBoard(-3, 1, "table-button-pressed1")
        replaceInGameBoard(-2, 1, "table-button-pressed1")
        replaceInGameBoard(-1, 1, "table-button-pressed1")
        if(gamemode == Gamemode.Hardcore || gamemode == Gamemode.FiveLife) checkSolution(false)
      }
    } else if(mode == "left") {
      if(drag) {
        replaceInGameBoard(-3,0,"table-button")
        replaceInGameBoard(-2,2,"table-button-pressed2")
        replaceInGameBoard(-1,1,"table-button-pressed1")
        drawDrag(x,y,x1,y1,"table-button-pressed2")
      } else {
        replaceInGameBoard(-3, 2, "table-button-pressed2")
        replaceInGameBoard(-2, 2, "table-button-pressed2")
        replaceInGameBoard(-1, 2, "table-button-pressed2")
        if(gamemode == Gamemode.Hardcore || gamemode == Gamemode.FiveLife) checkSolution(false)
      }
    }
  }

  def replaceInGameBoard(toreplace: Int, replacewith: Int, buttonvalue: String): Unit = {
    for(y <- 1 until gameboard.length) {
      for(x <- 1 until gameboard.length) {
        if(gameboard(y)(x) == toreplace) {
          gameboard(y)(x) = replacewith
          helper.getElementByID(x+"|"+y).setAttribute("class", buttonvalue)
          //helper.editElementByID("d"+x+"|"+y, replacewith.toString)
        }
      }
    }
  }

  def drawDrag(Bx: Int, By: Int, x1: Int, y1: Int, buttonclass: String): Unit = {
    val diffx = (Bx - x1).abs
    val diffy = (By - y1).abs
    var a = 0
    var b = 0

    if(diffx >= diffy) {
      b = Bx; a = x1
      if (Bx < x1) {
        a = Bx
        b = x1
      }
      for(x <- a to b) {
        helper.getElementByID(x+"|"+y1).setAttribute("class", buttonclass)
        if (gameboard(y1)(x) == 0) gameboard(y1)(x) = -3
        else gameboard(y1)(x) = -Math.abs(gameboard(y1)(x))
        //helper.editElementByID("d"+x+"|"+y1, gameboard(y1)(x).toString)
      }
    } else {
      b = By; a = y1
      if (By < y1) {
        a = By
        b = y1
      }
      for(y <- a to b) {
        helper.getElementByID(x1+"|"+y).setAttribute("class", buttonclass)
        if (gameboard(y)(x1) == 0) gameboard(y)(x1) = -3
        else gameboard(y)(x1) = -Math.abs(gameboard(y)(x1))
        //helper.editElementByID("d"+x1+"|"+y, gameboard(y)(x1).toString)
      }
    }
  }

  def createParser(size: String): Unit = {
    size match {
      case "5 x 5" => puzzle = parser.parseDefinition(Heart.puzzle)
      case "10 x 10" => puzzle = parser.parseDefinition(MrKrabs.puzzle)
      case "15 x 15" => puzzle = parser.parseDefinition(House.puzzle)
      case "20 x 20" => puzzle = parser.parseDefinition(SailingShip.puzzle)
      case _ => puzzle = parser.parseDefinition(Heart.puzzle)
    }

    solver = new Solver
    solver.solve(puzzle)
  }

  def checkSolution(checkall: Boolean): Unit = {
    var completecheck = solver.submitSolution(gameboard)
    if(completecheck) menureference.winMenu()

    if(checkall) {
      menureference.looseMenu()
    } else {
      var check = !solver.checkPosition(gameboard)
      if (check) {
        if (gamemode == Gamemode.FiveLife) {
          lives -= 1
          helper.getElementByID("spacer").textContent = "Lives: " + lives
          if (lives <= 0) menureference.looseMenu()
        } else if (gamemode == Gamemode.Hardcore) {
          menureference.looseMenu()
        }
      }
    }
  }

  def setGameMode(mode: String): Unit  =  {
    mode match {
      case "5 Lives Mode" =>
        gamemode = Gamemode.FiveLife
        helper.getElementByID("spacer").textContent = "Lives: " + lives
      case "Hardcore Mode" => gamemode = Gamemode.Hardcore
      case _ => gamemode = Gamemode.Default
    }
  }
}
