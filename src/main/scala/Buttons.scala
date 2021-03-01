import Enums.{GameMode, State}
import Menus.gameContext
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element


object Buttons {
  var playfield:PlayField = null
  private var wrongField: Array[Array[Int]] = Array[Array[Int]]()

  var gameboard:Array[Array[Int]] = Array()
  var last_x = -1
  var last_y = -1

  def createButton(text: String, classname: String, targetNode: dom.Node, eventFunc: () => Unit = null, id: String = ""): Element = {
    val button = document.createElement("button")
    button.textContent = text
    button.setAttribute("class",classname)

    if (eventFunc != null) {
      button.addEventListener("click", {_: dom.MouseEvent =>
        eventFunc()
      })
    }

    if (id != "") button.id = id
    targetNode.appendChild(button)
    return button
  }

  def buttonHover(x: Int, y: Int, style: String): Unit = {
    val size_x = gameContext.puzzle.rowSegments(y - 1).length
    val size_y = gameContext.puzzle.colSegments(x - 1).length

    for(y1 <- gameContext.puzzle.getColSize() until gameContext.puzzle.getColSize()-size_y by -1) {
      document.getElementById("r"+x+"|"+y1).setAttribute("class", style)
    }
    for(x1 <- gameContext.puzzle.getRowSize() until gameContext.puzzle.getRowSize()-size_x by -1) {
      document.getElementById("c"+x1+"|"+y).setAttribute("class", style)
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
        if(x == x1 && y ==y1) {
          replaceInGameBoard(-1, 0, "table-button")
        } else {
          replaceInGameBoard(-1, 1, "table-button-pressed1")
        }
        if(gameContext.mode == GameMode.Hardcore || gameContext.mode == GameMode.FiveLives) checkSolution(false)
      }
    } else if(mode == "left") {
      if(drag) {
        replaceInGameBoard(-3,0,"table-button")
        replaceInGameBoard(-2,2,"table-button-pressed2")
        replaceInGameBoard(-1,1,"table-button-pressed1")
        drawDrag(x,y,x1,y1,"table-button-pressed2")
      } else {
        if(x == x1 && y ==y1) {
          replaceInGameBoard(-2, 0, "table-button")
        } else {
          replaceInGameBoard(-2, 2, "table-button-pressed2")
        }
        replaceInGameBoard(-3, 2, "table-button-pressed2")
        replaceInGameBoard(-1, 2, "table-button-pressed2")
        if(gameContext.mode == GameMode.Hardcore || gameContext.mode == GameMode.FiveLives) checkSolution(false)
      }
    }
  }

  def replaceInGameBoard(toreplace: Int, replacewith: Int, buttonvalue: String): Unit = {
    for(y <- 1 until gameboard.length) {
      for(x <- 1 until gameboard.length) {
        if(gameboard(y)(x) == toreplace) {
          gameboard(y)(x) = replacewith
          document.getElementById(x+"|"+y).setAttribute("class", buttonvalue)
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
        document.getElementById(x+"|"+y1).setAttribute("class", buttonclass)
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
        document.getElementById(x1+"|"+y).setAttribute("class", buttonclass)
        if (gameboard(y)(x1) == 0) gameboard(y)(x1) = -3
        else gameboard(y)(x1) = -Math.abs(gameboard(y)(x1))
      }
    }
  }

  def checkSolution(checkall: Boolean): Unit = {
    var completecheck = submitSolution(gameboard)
    if(completecheck) {
      if(!gameContext.gameOver) Menus.winMenu()
      gameContext.gameOver = true
      return
    }

    if(checkall) {
      Menus.looseMenu()
    } else {
      val check = checkPosition(gameboard)
      if (!check._1) {
        if (gameContext.mode == GameMode.FiveLives) {
          if (check._3 > 0 && !gameContext.gameOver) {
            for(y <- check._2.indices) {
              for(x <- check._2.indices) {
                if (check._2(y)(x) == 1) {
                  gameboard(y+1)(x+1) = 2
                  document.getElementById((x+1)+"|"+(y+1)).setAttribute("class", "table-button-pressed2")
                }
              }
            }
            completecheck = submitSolution(gameboard)
            if(completecheck) {
              if(!gameContext.gameOver) Menus.winMenu()
              gameContext.gameOver = true
            }
          }
          if(!gameContext.gameOver) {
            for (l <- gameContext.lives - check._3 until gameContext.lives) {
              if (gameContext.lives-1 >= 0 ) document.getElementById("heart" + (gameContext.lives - 1)).setAttribute("class", "brokenheart")
              gameContext.lives -= 1
            }
          }
          if (gameContext.lives <= 0) {
            if(!gameContext.gameOver) Menus.looseMenu()
            gameContext.gameOver = true
          }
        } else if (gameContext.mode == GameMode.Hardcore) {
          if(!gameContext.gameOver) Menus.looseMenu()
          gameContext.gameOver = true
        }
      }
    }
  }

  def submitSolution(submission: Array[Array[Int]]): Boolean = {
    for(y <- 0 until gameContext.size) {
      for(x <- 0 until gameContext.size) {
        if(gameContext.solution(y)(x) == State.Set && submission(y+1)(x+1) != State.Set) return false
        if(gameContext.solution(y)(x) == State.Blank && submission(y+1)(x+1) == State.Set) return false
      }
    }
    true
  }

  def checkPosition(submission: Array[Array[Int]]): (Boolean, Array[Array[Int]], Int) = {
    wrongField = Array.ofDim[Int](gameContext.size, gameContext.size)
    var check = true
    var count = 0

    for(y <- wrongField.indices) {
      for(x <- wrongField.indices) {
        wrongField(y)(x) = 0
      }
    }

    for(y <- 0 until gameContext.size) {
      for(x <- 0 until gameContext.size) {
        if(submission(y+1)(x+1) == State.Set && gameContext.solution(y)(x) != State.Set) {
          check = false
          wrongField(y)(x) = 1
          count += 1
        }
      }
    }
    return (check, wrongField, count)
  }
}
