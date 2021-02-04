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

  def main(args: Array[String]): Unit = {
    // parsing example
    val puzzle = parser.parseDefinition(Heart.puzzle)

    // solving example
    val solver = new Solver
    val solved = solver.solve(puzzle)
    println(solved.map(row => row.map(e => if (e == 1) "#" else " ")).map(_.mkString("|")).mkString("\n"))

    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      setupUI()
      parseFile("level1.txt")
    })
  }

  def setupUI(): Unit = {
    appendElement(document.body,"div", "main-menu", "main-menu")
    var mainmenu = getElementByID("main-menu")
    appendElement(mainmenu, "div", "row1","row1" )
    appendElement(mainmenu, "div", "row2","row2" )
    var row1 = getElementByID("row1")
    var row2 = getElementByID("row2")

    createButton("Play Nonogram","menu-button",row1,true, createGame)
    createButton("Solver","menu-button",row1,true, createGame)
    createButton("Rules","menu-button",row1,true, createGame)
    createButton("Options1","menu-button",row2,true, createGame)
    createButton("Options2","menu-button",row2,true, createGame)
    createButton("Options3","menu-button",row2,true, createGame)
  }

  def parseFile(level: String): Unit = {
    parser.currentlevel = parser.level1
  }

  def createGame(): Unit = {
    removeElementByID("main-menu")

    var y = parser.getTableColSize() + parser.getColSize()
    var x = parser.getTableRowSize() + parser.getRowSize()

    var createplayfield = new createplayfield(
      parser.getTableRowSize(),
      parser.getTableColSize(),
      parser.getRowSize(),
      parser.getColSize(),
    )
    gameboard = createplayfield.initGameBoard()
    document.body.appendChild(createplayfield.createPlayTable(
      parser.getRowsOfCurrentLevel(),
      parser.getColsOfCurrentLevel(),
      buttonFunction))
    document.body.appendChild(createplayfield.createDebugGameBoard(gameboard))
  }

  def buttonFunction(button: Element, x: Int, y: Int, x1: Int, y1: Int, commit: Boolean): Unit ={
    var n = 0

    if (!commit) {
      drawDrag(button,x,y,x1,y1)
      return
    }

    if (gameboard(y)(x) == 0) {
      n = 1
    } else if (gameboard(y)(x) == 1) {
      n = 2
    } else {
      n = 0
    }
    button.setAttribute("class", "table-button-pressed1")
    gameboard(y)(x) = n
    editElementByID("d"+x+"|"+y, n.toString)
  }

  def drawDrag(button: Element, x: Int, y: Int, x1: Int, y1: Int): Unit = {
    var diffx = (x - x1).abs
    var diffy = (y - y1).abs


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