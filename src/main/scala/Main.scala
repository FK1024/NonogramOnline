import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

object Main {
  var id = 0
  var collection:Map[String,Int] = Map()

  def main(args: Array[String]): Unit = {
    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      setupUI()
    })
  }

  def setupUI(): Unit = {
    createButton("Click me!", true,addClickedMessage)
    createButton("Play Nonogram", true, createGame)
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    parNode.textContent = text
    targetNode.appendChild(parNode)
  }

  def addClickedMessage(): Unit = {
    appendPar(document.body, "You clicked the butt!")
  }

  def createGame(): Unit = {
    removeElementByName("Play Nonogram")
    removeElementByName("Click me!")

    var createplayfield = new createplayfield(5,6)
    document.body.appendChild(createplayfield.appendPar("1"))
  }

  def createButton(text: String, event: Boolean, eventfunc: () => Unit): Element = {
    val button = document.createElement("button")
    button.textContent = text
    button.id = id.toString
    collection = collection ++ Map(text -> id)
    id += 1

    if (event) {
      button.addEventListener("click", {(e: dom.MouseEvent) =>
        eventfunc()
      })
    }
    document.body.appendChild(button)

    return button
  }

  def removeElementByName(name: String): Unit = {
    var elementid = collection.get(name).get.toString

    println(document.getElementById(elementid).textContent)
    document.body.removeChild(document.getElementById(elementid))
  }
}