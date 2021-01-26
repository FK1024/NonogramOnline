import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

class createplayfield(x: Int, y: Int) {

  def appendPar(text: String): Element = {
    var table = document.createElement("table")
    table.setAttribute("class","styled-table")

    for(i <- 0 to y-1) {
      var tablerow = document.createElement("tr")
      for(j <- 0 to x-1) {
        var content = document.createElement("th")
        if(j==0) content.textContent = i.toString
        if(i==0) content.textContent = j.toString
        tablerow.appendChild(content)
      }
      table.appendChild(tablerow)
    }
    return table
  }
}
