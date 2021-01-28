import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

object Main {
  var id = 0
  var collection:Map[String,Int] = Map()
  var gameboard:Array[Array[Int]] = Array()
  var parser = new parser()

  def main(args: Array[String]): Unit = {
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

  def buttonFunction(button: Element, x: Int, y: Int): Unit ={
    var s = " "
    var n = 0

    if (gameboard(y)(x) == 0) {
      s = "X"
      n = 1
    } else if (gameboard(y)(x) == 1) {
      s = "-"
      n = 2
    } else {
      s = " "
      n = 0
    }

    button.textContent = s
    gameboard(y)(x) = n
    editElementByID("d"+x+"|"+y, n.toString)
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