import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element

class CreatePlayField(tablerows: Int, tablecols: Int, rows: Int, cols: Int) {

  val y = cols + tablecols
  val x = rows + tablerows

  var dragx = -1
  var dragy = -1
  var drag = false
  var mode = "right"

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

  def createPlayTable(currentrows: List[List[Int]], currentcols: List[List[Int]], buttonFunction: (Int, Int, Int, Int, Boolean, Boolean, String) => Unit): Element = {
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
              div.id = "c"+(j+1)+"|"+(i-cols+1)
              if (j == rows-1) content.setAttribute("class", "th2")
              else content.setAttribute("class", "th1")
            }
          }
        } else if(i < cols) {
          if(j >= rows) {
            var r = currentcols(j-rows)
            if (r.length >= cols-i) {
              div.textContent = r(r.length-cols+i).toString
              div.id = "r"+(j-rows+1)+"|"+(i+1)
              if (i == cols-1) content.setAttribute("class", "th3")
              else content.setAttribute("class", "th1")
            }
          }
        } else {
          div.appendChild(createTableButton(j-rows+1,i-cols+1, buttonFunction))
          content.setAttribute("class", "th1")
        }
        content.appendChild(div)
        tablerow.appendChild(content)
      }
      table.appendChild(tablerow)
    }
    return table
  }

  def createTableButton(Bx: Int, By: Int, buttonFunction: (Int, Int, Int, Int, Boolean, Boolean, String) => Unit): Element = {
    val button = document.createElement("button")
    button.setAttribute("class","table-button")
    button.textContent = " "
    button.id = Bx+"|"+By
    button.addEventListener("mousedown", {e: dom.MouseEvent =>
      if (drag) {
        drag = false
        if(e.button == 0) mode = "right"
        if(e.button == 2) mode = "left"
        buttonFunction(Bx, By, dragx, dragy, drag, false, mode)
      } else {
        dragx = Bx
        dragy = By
        drag = true
        if(e.button == 0) mode = "right"
        if(e.button == 2) mode = "left"
        buttonFunction(Bx, By, dragx, dragy, drag, false, mode)
      }
    })
    button.addEventListener("mouseup", { e: dom.MouseEvent =>
      drag = false
      if(e.button == 0) mode = "right"
      if(e.button == 2) mode = "left"
      buttonFunction(Bx, By, dragx, dragy, drag, false, mode)
    })
    button.addEventListener("mouseover", {e: dom.MouseEvent =>
      if (drag) {
        buttonFunction(Bx, By, dragx, dragy, drag, false, mode)
      }
      buttonFunction(Bx, By, dragx, dragy, drag, true, mode)
    })
    button.addEventListener("contextmenu", {e: dom.MouseEvent =>
      e.preventDefault()
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
