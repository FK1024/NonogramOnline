import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

object Dimension extends Enumeration {
  type Dimension = Value
  val Row, Column = Value
}

object Main {
  var id = 0
  var collection:Map[String,Int] = Map()
  var gameboard:Array[Array[Int]] = Array()
  var parser = new Parser()
  var puzzle:Puzzle = null
  var playfield:CreatePlayField = null
  var solver:Solver = null

  var last_x = -1
  var last_y = -1

  def main(args: Array[String]): Unit = {
    // parsing example
    puzzle = parser.parseDefinition(Heart.puzzle)

    // solving example
    solver = new Solver
    solver.solve(puzzle)

    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      setupUI()
    })
  }

  def setupUI(): Unit = {
    appendElement(document.body,"div", "menu", "main-menu")
    var mainmenuElement = getElementByID("main-menu")

    createButton("Play Nonogram","menu-button",mainmenuElement,true, createGame)
    createButton("Solver","menu-button",mainmenuElement,true, createGame)
    createButton("Rules","menu-button",mainmenuElement,true, createGame)
  }

  def createGame(): Unit = {
    removeElementByID("main-menu")
    appendElement(document.body,"div", "playfield", "playfield")
    var playfieldElement = getElementByID("playfield")

    var y = puzzle.getColSegmentSize() + puzzle.getColSize()
    var x = puzzle.getRowSegmentSize() + puzzle.getRowSize()

    //println(puzzle.getColSegmentSize()) // 5
    //println(puzzle.getColSize()) // 1

    playfield = new CreatePlayField(
      puzzle.getRowSegmentSize(),
      puzzle.getColSegmentSize(),
      puzzle.getRowSize(),
      puzzle.getColSize(),
    )
    gameboard = playfield.initGameBoard()
    playfieldElement.appendChild(playfield.createPlayTable(
      puzzle.rowSegments,
      puzzle.colSegments,
      buttonFunction))
    playfieldElement.appendChild(playfield.createDebugGameBoard(gameboard))

    appendElement(playfieldElement, "div", "menu","menu")
    var row1 = getElementByID("menu")
    createButton("Back","menu-button",row1,true, ()=>backToMenu("playfield", setupUI))
    createButton("Check","menu-button",row1,true, checkSolution)
  }

  def backToMenu(toremove: String, eventfunc: () => Unit): Unit = {
    removeElementByID(toremove)
    eventfunc()
  }

  def checkSolution(): Unit = {
    if(solver.submitSolution(gameboard)) {
      println("Nice!!")
    } else {
      println("Try again!")
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

  def buttonHover(x: Int, y: Int, style: String): Unit = {
    var size_x = puzzle.rowSegments(y-1).length
    var size_y = puzzle.colSegments(x-1).length

    for(y1 <- puzzle.getColSize() until puzzle.getColSize()-size_y by -1) {
      getElementByID("r"+x+"|"+y1).setAttribute("class", style)
    }
    for(x1 <- puzzle.getRowSize() until puzzle.getRowSize()-size_x by -1) {
      getElementByID("c"+x1+"|"+y).setAttribute("class", style)
    }
  }

  // TODO: refactor this function !!!
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
      }
    }
  }

  def replaceInGameBoard(toreplace: Int, replacewith: Int, buttonvalue: String): Unit = {
    for(y <- 1 until gameboard.length) {
      for(x <- 1 until gameboard.length) {
        if(gameboard(y)(x) == toreplace) {
          gameboard(y)(x) = replacewith
          getElementByID(x+"|"+y).setAttribute("class", buttonvalue)
          editElementByID("d"+x+"|"+y, replacewith.toString)
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
        getElementByID(x+"|"+y1).setAttribute("class", buttonclass)
        if (gameboard(y1)(x) == 0) gameboard(y1)(x) = -3
        else gameboard(y1)(x) = -Math.abs(gameboard(y1)(x))
        editElementByID("d"+x+"|"+y1, gameboard(y1)(x).toString)
      }
    } else {
      b = By; a = y1
      if (By < y1) {
        a = By
        b = y1
      }
      for(y <- a to b) {
        getElementByID(x1+"|"+y).setAttribute("class", buttonclass)
        if (gameboard(y)(x1) == 0) gameboard(y)(x1) = -3
        else gameboard(y)(x1) = -Math.abs(gameboard(y)(x1))
        editElementByID("d"+x1+"|"+y, gameboard(y)(x1).toString)
      }
    }
  }

  //--------------------------------------------------------------------------------
  // Helper Functions: TODO auslagern ?
  def createButton(text: String, classname: String, targetNode: dom.Node, event: Boolean, eventfunc: () => Unit): Element = {
    val button = document.createElement("button")
    button.textContent = text
    button.id = id.toString
    collection = collection ++ Map(text -> id)
    id += 1
    button.setAttribute("class",classname)

    if (event) {
      button.addEventListener("click", {e: dom.MouseEvent =>
        eventfunc()
      })
    }
    targetNode.appendChild(button)
    return button
  }

  def removeElementByName(name: String): Unit = {
    var elementid = collection(name).toString
    var parentnode = document.getElementById(elementid).parentNode
    parentnode.removeChild(document.getElementById(elementid))
  }

  def removeElementByID(id: String): Unit = {
    var parentnode = document.getElementById(id).parentNode
    parentnode.removeChild(document.getElementById(id))
  }

  def editElementByID(name: String, newtext: String): Unit =  {
    document.getElementById(name).textContent = newtext
  }

  def getElementByID(name: String): Element = {
    return document.getElementById(name)
  }

  def appendElement(targetNode: dom.Node, element: String, classname: String, id: String): Unit = {
    val parNode = document.createElement(element)
    parNode.id = id
    parNode.setAttribute("class",classname)
    targetNode.appendChild(parNode)
  }
}