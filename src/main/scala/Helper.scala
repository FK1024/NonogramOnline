import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

object Helper {

  def removeElementByID(id: String): Unit = {
    var myNode = document.getElementById(id)
    if (myNode != null) {
      myNode.parentNode.removeChild(document.getElementById(id))
    }
  }

  def editElementByID(id: String, newtext: String): Unit =  {
    document.getElementById(id).textContent = newtext
  }

  def getElementByID(id: String): Element = {
    return document.getElementById(id)
  }

  def appendElement(targetNode: dom.Node, element: String, classname: String, id: String): Unit = {
    val parNode = document.createElement(element)
    parNode.id = id
    parNode.setAttribute("class",classname)
    targetNode.appendChild(parNode)
  }

  def rules1(): String = {
    "Your aim in these puzzles is to colour the whole grid into black and white squares. " +
    "Leaving one empty, is equal to marking it white. " +
    "Beside each row of the grid are listed the lengths of the runs of black squares on that row. " +
    "Above each column are listed the lengths of the runs of black squares in that column. " +
    "These numbers tell you the runs of black squares in that row/column. So, if you see '10 1', " +
    "that tells you that there will be a run of exactly 10 black squares, followed by one or more white square, followed by a single black square. " +
    "There may be more white squares before/after this sequence. "
  }

  def rules2(): String = {
    "Left click on a square to make it black. Right click to mark it white. Click and drag to mark more than one square"
  }

  def rules3(): String = {
    "To solve a puzzle, one needs to determine which cells will be boxes and which will be empty. " +
      "Determining which cells are to be left empty is as important as determining which to fill. " +
      "Later in the solving process, the spaces help determine where a clue may spread. " +
      "Solvers usually use a dot or a cross to mark cells they are certain are spaces. " +
      "It is also important never to guess. Only cells that can be determined by logic should be filled. " +
      "If guessing, a single error can spread over the entire field and completely ruin the solution."
  }
}
