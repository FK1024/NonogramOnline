import Enums.NodeType
import Enums.NodeType.NodeType
import org.scalajs.dom
import org.scalajs.dom.{Node, document}

object DomHelper {
  def removeElementByID(id: String): Unit = {
    val myNode = document.getElementById(id)
    if (myNode != null) {
      myNode.parentNode.removeChild(document.getElementById(id))
    }
  }

  def appendElement(nodeType: NodeType, targetNode: Node, classname: String = "", id: String = "", text: String = ""): dom.Element = {
    val nodeTypeStr = nodeType match {
      case NodeType.Div => "div"
      case NodeType.Table => "table"
      case NodeType.Tr => "tr"
      case NodeType.Th => "th"
      case NodeType.Br => "br"
    }
    val newNode = document.createElement(nodeTypeStr)
    if (classname != "") newNode.setAttribute("class", classname)
    if (id != "") newNode.id = id
    if (text != "") newNode.textContent = text
    targetNode.appendChild(newNode)
    return newNode
  }
}
