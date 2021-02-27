import scala.collection.mutable.ListBuffer

object Parser {
  /**
   * parses a given data file in which
   * 'r:' indicates, that the following lines of numbers are row segments and
   * 'c:' indicates, that the following lines of numbers are column segments
   *
   * @param puzzleDefinition string containing the puzzle definition
   * @return a tuple containing the row and column segments
   */
  def parseDefinition(puzzleDefinition: String) = {
    val rowSegments = new ListBuffer[List[Int]]()
    val colSegments = new ListBuffer[List[Int]]()

    val trimmed = trimLineBreaks(puzzleDefinition)
    val lines = trimmed.split("\r\n", 0)

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

  def trimLineBreaks(text: String) = {
    "^[\r\n]+|\\.|[\r\n]+$".r.replaceAllIn(text.trim, "")
  }
}
