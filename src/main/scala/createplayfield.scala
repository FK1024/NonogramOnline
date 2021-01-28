import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

class createplayfield(tablerows: Int, tablecols: Int, rows: Int, cols: Int) {

  val y = cols + tablecols
  val x = rows + tablerows

  var dragx = -1
  var dragy = -1
  var drag = false

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

  def createPlayTable(currentrows: Array[String], currentcols: Array[String], buttonFunction: (Element, Int, Int, Int, Int, Boolean) => Unit): Element = {
    var table = document.createElement("table")
    table.setAttribute("class","styled-table")

    for(i <- 0 until y) {
      var tablerow = document.createElement("tr")
      for(j <- 0 until x) {
        var content = document.createElement("th")
        var div = document.createElement("div")
        div.setAttribute("class", "table-size")
        if(j < rows) {
          if(i >= cols) {
            var s = currentrows(i-cols).split(" ")
            if(s.length >= rows-j) {
              div.textContent = s(s.length-rows+j)
            }
          }
        } else if(i < cols) {
          if(j >= rows) {
            var s = currentcols(j-rows).split(" ")
            if (s.length >= cols-i) {
              div.textContent = s(s.length-cols+i)
            }
          }
        } else {
          div.appendChild(createTableButton(j-rows+1,i-cols+1, buttonFunction))
        }
        content.appendChild(div)
        tablerow.appendChild(content)
      }
      table.appendChild(tablerow)
    }
    return table
  }

  def createTableButton(Bx: Int, By: Int, buttonFunction: (Element, Int, Int, Int, Int, Boolean) => Unit): Element = {
    val button = document.createElement("button")
    button.setAttribute("class","table-button")
    button.textContent = " "
    button.id = Bx+"|"+By
    button.addEventListener("mousedown", {e: dom.MouseEvent =>
      dragx = Bx
      dragy = By
      drag = true
      buttonFunction(button, Bx, By, -1, -1, false)
    })
    button.addEventListener("mouseup", { e: dom.MouseEvent =>
      buttonFunction(button, Bx, By, dragx, dragy, true)
      drag = false
    })
    button.addEventListener("mouseover", {e: dom.MouseEvent =>
      if (drag) {
        dragx = Bx
        dragy = By
        buttonFunction(button, Bx, By, dragx, dragy, false)
      }
    })

    return button
  }

  //-------------------------------------------------------------
  // Only for Debug TODO remove when project is finished
  def createDebugGameBoard(gameboard: Array[Array[Int]]): Element = {
    var table = document.createElement("table")
    table.setAttribute("class","styled-table")

    for(i <- 0 to tablecols) {
      var tablerow = document.createElement("tr")
      for(j <- 0 to tablerows) {
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
}
