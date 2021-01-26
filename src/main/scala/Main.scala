import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

object Main {
  var id = 0
  var collection:Map[String,Int] = Map()
  var gameboard:Array[Array[Int]] = Array()

  def main(args: Array[String]): Unit = {
    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      setupUI()
      parseFile("level1.txt")
    })
  }

  def setupUI(): Unit = {
    createButton("Play Nonogram", true, createGame)
  }

  def parseFile(level: String): Unit = {
    val reader = new dom.FileReader()
  }

  def createGame(): Unit = {
    removeElementByName("Play Nonogram")
    var createplayfield = new createplayfield(5,6)
    gameboard = createplayfield.initGameBoard()
    document.body.appendChild(createplayfield.createPlayTable(buttonFunction))
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
    editElementByName("d"+x+"|"+y, n.toString)
  }

  //--------------------------------------------------------------------------------
  // Helper Functions: TODO auslagern ?

  def createButton(text: String, event: Boolean, eventfunc: () => Unit): Element = {
    val button = document.createElement("button")
    button.textContent = text
    button.id = id.toString
    collection = collection ++ Map(text -> id)
    id += 1

    if (event) {
      button.addEventListener("click", {e: dom.MouseEvent =>
        eventfunc()
      })
    }
    document.body.appendChild(button)
    return button
  }

  def removeElementByName(name: String): Unit = {
    var elementid = collection(name).toString
    document.body.removeChild(document.getElementById(elementid))
  }

  def editElementByName(name: String, newtext: String): Unit =  {
    document.getElementById(name).textContent = newtext
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    parNode.textContent = text
    targetNode.appendChild(parNode)
  }
}