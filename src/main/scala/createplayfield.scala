import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

class createplayfield(x: Int, y: Int) {

  def appendPar(): Element = {
    var table = document.createElement("table")
    table.setAttribute("class","styled-table")

    for(i <- 0 to y) {
      var tablerow = document.createElement("tr")
      for(j <- 0 to x) {
        var content = document.createElement("th")
        if(j==0) {
          content.textContent = i.toString
        } else if(i==0) {
          content.textContent = j.toString
        } else {
          content.appendChild(createTableButton(j,i))
        }
        tablerow.appendChild(content)
      }
      table.appendChild(tablerow)
    }
    return table
  }

  def createTableButton(x: Int, y: Int): Element = {
    val button = document.createElement("button")
    button.setAttribute("class","table-button")
    button.textContent = x+"|"+y
    button.addEventListener("click", {(e: dom.MouseEvent) =>
      buttonFunction(x,y)
    })
    return button
  }

  def buttonFunction(x: Int, y: Int): Unit ={
    println(x+"|"+y)
  }
}
