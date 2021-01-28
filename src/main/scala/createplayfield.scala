import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

class createplayfield(x: Int, y: Int) {

  def initGameBoard(): Array[Array[Int]] = {
    var gameboard:Array[Array[Int]] = Array()

    for(i <- 0 to y) {
      var h:Array[Int] = Array()
      for(j <- 0 to x) {
        h = h :+ 0
      }
      gameboard = gameboard :+ h
    }
    return gameboard
  }


  def createPlayTable(buttonFunction: (Element, Int, Int) => Unit): Element = {
    var table = document.createElement("table")
    table.setAttribute("class","styled-table")

    for(i <- 0 to y) {
      var tablerow = document.createElement("tr")
      for(j <- 0 to x) {
        var content = document.createElement("th")
        var div = document.createElement("div")
        div.setAttribute("class", "table-size")
        if(j==0) {
          div.textContent = i.toString
        } else if(i==0) {
          div.textContent = j.toString
        } else {
          div.appendChild(createTableButton(j,i, buttonFunction))
        }
        content.appendChild(div)
        tablerow.appendChild(content)
      }
      table.appendChild(tablerow)
    }
    return table
  }

  // Only for Debug TODO remove when project is finished
  def createDebugGameBoard(gameboard: Array[Array[Int]]): Element = {
    var table = document.createElement("table")
    table.setAttribute("class","styled-table")

    for(i <- 0 to y) {
      var tablerow = document.createElement("tr")
      for(j <- 0 to x) {
        var content = document.createElement("th")
        var div = document.createElement("div")
        div.setAttribute("class", "table-size")
        if(j==0) {
          div.textContent = i.toString
        } else if(i==0) {
          div.textContent = j.toString
        } else {
          if (j <= x || i <= y-1) {
            div.textContent = gameboard(y)(x).toString
            div.id = "d"+j+"|"+i
          }
        }
        content.appendChild(div)
        tablerow.appendChild(content)
      }
      table.appendChild(tablerow)
    }
    return table
  }

  def createTableButton(x: Int, y: Int, buttonFunction: (Element, Int, Int) => Unit): Element = {
    val button = document.createElement("button")
    button.setAttribute("class","table-button")
    button.textContent = " "
    button.id = x+"|"+y
    button.addEventListener("click", {e: dom.MouseEvent =>
      buttonFunction(button, x,y)
    })
    return button
  }
}
