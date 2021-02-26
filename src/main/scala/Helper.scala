import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

class Helper {

  def removeElementByID(id: String): Unit = {
    var myNode = document.getElementById(id)
    if (myNode != null) {
      myNode.parentNode.removeChild(document.getElementById(id))
    }
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
