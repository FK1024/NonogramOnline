import org.scalajs.dom

import scala.collection.mutable.ListBuffer
import scala.io.Source

class Parser {
  var level1 = "c|2|4|4|4|2|r|1 1|5|5|3|1"
  var currentlevel = level1

  /**
   * parses a given data file in which
   * 'r:' indicates, that the following lines of numbers are row segments and
   * 'c:' indicates, that the following lines of numbers are column segments
   *
   * @param file the file containing the puzzle data
   * @return a tuple containing the row and column segments
   */
  def parseFile(file: String) = {
    val rowSegments = new ListBuffer[List[Int]]()
    val colSegments = new ListBuffer[List[Int]]()

    val source = Source.fromFile(file)
    val lines = try source.getLines().toList finally source.close()

    // initialize parse mode with what the first line indicates
    var parseMode = lines.head match {
      case "r:" => Dimension.Row
      case "c:" => Dimension.Column
      case _ => throw new Exception(s"the first line of data file has to begin either with 'r:' or with 'c:' but it begins with '${lines.head}''")
    }

    // read the remaining lines and change parse mode if necessary
    for (line <- lines.drop(1)) {
      line match {
        case "r:" => parseMode = Dimension.Row
        case "c:" => parseMode = Dimension.Column
        case _ => {
          val numbers = line.split(" ").map(_.toInt).toList
          parseMode match {
            case Dimension.Row => rowSegments.append(numbers)
            case Dimension.Column => colSegments.append(numbers)
          }
        }
      }
    }

    // check the input for being a quadratic puzzle
    val rowCount = rowSegments.length
    val colCount = colSegments.length
    if (rowCount != colCount) throw new Exception(s"there has to be an equal amount of lines for row and column number but there were $rowCount for rows and $colCount for columns")

    new Puzzle(rowSegments.toList, colSegments.toList)
  }

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
