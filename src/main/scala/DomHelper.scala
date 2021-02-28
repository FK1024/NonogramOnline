import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

object DomHelper {

  def removeElementByID(id: String): Unit = {
    val myNode = document.getElementById(id)
    if (myNode != null) {
      myNode.parentNode.removeChild(document.getElementById(id))
    }
  }

  def appendElement(targetNode: dom.Node, element: String, classname: String = "", id: String = ""): dom.Element = {
    val newNode = document.createElement(element)
    if (id != "") newNode.id = id
    if (classname != "") newNode.setAttribute("class",classname)
    targetNode.appendChild(newNode)
    return newNode
  }
}
