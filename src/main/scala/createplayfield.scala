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
    for(i <- 0 to tablecols) {
      var h:Array[Int] = Array()
      for(j <- 0 to tablerows) {
        h = h :+ 0
      }
      gameboard = gameboard :+ h
    }
    return gameboard
  }

  def createPlayTable(currentrows: List[List[Int]], currentcols: List[List[Int]], buttonFunction: (Int, Int, Int, Int, Boolean, dom.MouseEvent) => Unit): Element = {
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
            var r = currentrows(i-cols)
            if (r.length >= rows-j) {
              div.textContent = r(r.length-rows+j).toString
            }
          }
        } else if(i < cols) {
          if(j >= rows) {
            var r = currentcols(j-rows)
            if (r.length >= cols-i) {
              div.textContent = r(r.length-cols+i).toString
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

  def createTableButton(Bx: Int, By: Int, buttonFunction: (Int, Int, Int, Int, Boolean, dom.MouseEvent) => Unit): Element = {
    val button = document.createElement("button")
    button.setAttribute("class","table-button")
    button.textContent = " "
    button.id = Bx+"|"+By
    button.addEventListener("mousedown", {e: dom.MouseEvent =>
      dragx = Bx
      dragy = By
      drag = true
      buttonFunction(Bx, By, dragx, dragy, true, e)
    })
    button.addEventListener("mouseup", { e: dom.MouseEvent =>
      buttonFunction(Bx, By, dragx, dragy, false, e)
      drag = false
    })
    button.addEventListener("mouseover", {e: dom.MouseEvent =>
      if (drag) {
        buttonFunction(Bx, By, dragx, dragy, true, e)
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
          div.textContent = gameboard(i)(j).toString
          div.id = "d"+j+"|"+i
        }
        content.appendChild(div)
        tablerow.appendChild(content)
      }
      table.appendChild(tablerow)
    }
    return table
  }
}
