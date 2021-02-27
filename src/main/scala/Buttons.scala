import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element


object Buttons {
  var puzzle:Puzzle = null
  var playfield:PlayField = null

  var gamemode = Gamemode.Default
  var gameboard:Array[Array[Int]] = Array()
  var last_x = -1
  var last_y = -1
  var lives = 5
  var gameend = false

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
      Helper.getElementByID("r"+x+"|"+y1).setAttribute("class", style)
    }
    for(x1 <- puzzle.getRowSize() until puzzle.getRowSize()-size_x by -1) {
      Helper.getElementByID("c"+x1+"|"+y).setAttribute("class", style)
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
          Helper.getElementByID(x+"|"+y).setAttribute("class", buttonvalue)
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
        Helper.getElementByID(x+"|"+y1).setAttribute("class", buttonclass)
        if (gameboard(y1)(x) == 0) gameboard(y1)(x) = -3
        else gameboard(y1)(x) = -Math.abs(gameboard(y1)(x))
      }
    } else {
      b = By; a = y1
      if (By < y1) {
        a = By
        b = y1
      }
      for(y <- a to b) {
        Helper.getElementByID(x1+"|"+y).setAttribute("class", buttonclass)
        if (gameboard(y)(x1) == 0) gameboard(y)(x1) = -3
        else gameboard(y)(x1) = -Math.abs(gameboard(y)(x1))
      }
    }
  }

  def createParser(size: String): Unit = {
    size match {
      case "5 x 5" => puzzle = Parser.parseDefinition(Heart.puzzle)
      case "10 x 10" => puzzle = Parser.parseDefinition(MrKrabs.puzzle)
      case "15 x 15" => puzzle = Parser.parseDefinition(House.puzzle)
      case "20 x 20" => puzzle = Parser.parseDefinition(SailingShip.puzzle)
      case _ => puzzle = Parser.parseDefinition(Heart.puzzle)
    }

    Solver.solve(puzzle)
  }

  def checkSolution(checkall: Boolean): Unit = {
    var completecheck = Solver.submitSolution(gameboard)
    if(completecheck) {
      if(!gameend) Menus.winMenu()
      gameend = true
    }

    if(checkall) {
      Menus.looseMenu()
    } else {
      var check = Solver.checkPosition(gameboard)
      if (!check._1) {
        if (gamemode == Gamemode.FiveLife) {
          for(y <- check._2.indices) {
            for(x <- check._2.indices) {
              if (check._2(y)(x) == 1) {
                gameboard(y+1)(x+1) = 2
                Helper.getElementByID((x+1)+"|"+(y+1)).setAttribute("class", "table-button-pressed2")
              }
            }
          }
          if(!gameend) {
            for (l <- lives - check._3 until lives) {
              Helper.getElementByID("heart" + (lives - 1)).setAttribute("class", "brokenheart")
              lives -= 1
            }
          }
          if (lives <= 0) {
            if(!gameend) Menus.looseMenu()
            gameend = true
          }
        } else if (gamemode == Gamemode.Hardcore) {
          if(!gameend) Menus.looseMenu()
          gameend = true
        }
      }
    }
  }

  def setGameMode(mode: String): Unit  =  {
    mode match {
      case "5 Lives Mode" =>
        gamemode = Gamemode.FiveLife
        var spacer = Helper.getElementByID("spacer")
        Helper.appendElement(spacer, "div", "lives","lives")
        spacer = Helper.getElementByID("lives")
        Helper.appendElement(spacer, "div", "text","hearttext")
        spacer = Helper.getElementByID("hearttext")
        spacer.textContent = "Lives: "

        spacer = Helper.getElementByID("lives")
        for(i <- 0 to 4) {
          Helper.appendElement(spacer, "div", "heart","heart" + i)
        }

      case "Hardcore Mode" => gamemode = Gamemode.Hardcore
      case _ => gamemode = Gamemode.Default
    }
  }
}
