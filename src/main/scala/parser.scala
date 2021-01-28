class parser {
  var level1 = "c|2|4|4|4|2|r|1 1|5|5|3|1"
  var currentlevel = level1

  private def getColsOrRows(rows: Boolean): Array[String] = {
    var level = currentlevel.split("\\|")
    var result:Array[String] = Array()
    var r = rows

    for (s <- level) {
      if (s == "c") {
        r = !rows
      } else if (s == "r") {
        r = rows
      } else if (r) {
        result = result :+ s
      }
    }
    return result
  }

  private def getColsOrRowsSize(function: ()=> Array[String]): Int ={
    var table = function()
    var counter = 0
    for(t <- table) {
      if (counter < t.split(" ").length) {
        counter = t.split(" ").length
      }
    }
    return counter
  }

  //---------------------------------------
  def getRowsOfCurrentLevel(): Array[String] = {
    return getColsOrRows(true)
  }

  def getColsOfCurrentLevel(): Array[String] = {
    return getColsOrRows(false)
  }

  def getTableRowSize(): Int = {
    return getRowsOfCurrentLevel().length
  }

  def getTableColSize(): Int = {
    return getColsOfCurrentLevel().length
  }

  def getRowSize(): Int = {
    getColsOrRowsSize(getRowsOfCurrentLevel)
  }

  def getColSize(): Int = {
    getColsOrRowsSize(getColsOfCurrentLevel)
  }
}
