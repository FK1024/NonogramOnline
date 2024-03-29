import Enums.Dimension.Dimension
import Enums.{Dimension, State}

import scala.collection.mutable.ListBuffer

object Solver {
  private var fieldSize = 0
  private var gameField: Array[Array[Int]] = Array[Array[Int]]()

  def solve(puzzle: Puzzle): Array[Array[Int]] = {
    // initialize
    fieldSize = puzzle.rowSegments.length
    gameField = Array.ofDim[Int](fieldSize, fieldSize)

    // check for overfilled rows/cols
    for (i <- 0 until fieldSize) {
      if (getLaxity(puzzle.rowSegments(i)) < 0) {
        throw new Exception(s"The segments of row ${i + 1} are too long to fit in the game field")
      }
      if (getLaxity(puzzle.colSegments(i)) < 0) {
        throw new Exception(s"The segments of column ${i + 1} are too long to fit in the game field")
      }
    }

    var openRows = new ListBuffer[Int]
    var openCols = new ListBuffer[Int]
    var openSet = new ListBuffer[Int]

    var validRowConfigs = Array.ofDim[List[Array[Int]]](fieldSize)
    var validColConfigs = Array.ofDim[List[Array[Int]]](fieldSize)
    var validConfigs = Array.ofDim[List[Array[Int]]](fieldSize)
    for (i <- 0 until fieldSize) {
      validRowConfigs(i) = getConfigurations(puzzle.rowSegments(i))
      validColConfigs(i) = getConfigurations(puzzle.colSegments(i))
    }

    var solvedCell = true
    val solvedItems = new ListBuffer[Int]

    while (openRows.nonEmpty && openCols.nonEmpty && solvedCell) {
      solvedCell = false
      for (dim <- Dimension.values) {
        solvedItems.clear()

        dim match {
          case Dimension.Row =>
            openSet = openRows
            validConfigs = validRowConfigs
          case Dimension.Column =>
            openSet = openCols
            validConfigs = validColConfigs
        }

        for (index <- openSet) {
          validConfigs(index) = removeInvalidConfigs(dim, index, validConfigs(index))
          if (validConfigs(index).isEmpty) {
            throw new Exception("The puzzle was not solvable")
          }
          val transposed = validConfigs(index).transpose
          for (i <- transposed.indices) {
            if (dim == Dimension.Row && gameField(index)(i) == State.Unknown || dim == Dimension.Column && gameField(i)(index) == State.Unknown) {
              val cellStates = transposed(i)
              // all valid configurations for this previously unknown cell have the same state => can be set in gameField
              if (cellStates.forall(_ == cellStates.head)) {
                solvedCell = true
                dim match {
                  case Dimension.Row =>
                    gameField(index)(i) = cellStates.head
                    if (colSolved(i)) {
                      openCols -= i
                    }
                  case Dimension.Column =>
                    gameField(i)(index) = cellStates.head
                    if (rowSolved(i)) {
                      openRows -= i
                    }
                }
              }
            }
          }

          if (dim == Dimension.Row && rowSolved(index) || dim == Dimension.Column && colSolved(index)) {
            solvedItems.append(index)
          }
        }

        dim match {
          case Dimension.Row => openRows = openRows.filterNot(solvedItems.toSet)
          case Dimension.Column => openCols = openCols.filterNot(solvedItems.toSet)
        }
      }
    }

    if (!solvedCell) {
      throw new Exception("The puzzle was not solvable")
    } else {
      gameField
    }
  }

  def getLaxity(numbers: List[Int]): Int = fieldSize - (numbers.sum + numbers.length - 1)

  private def rowSolved(row: Int): Boolean = {
    for (col <- 0 until fieldSize) {
      if (gameField(row)(col) == State.Unknown) {
        return false
      }
    }
    true
  }

  private def colSolved(col: Int): Boolean = {
    for (row <- 0 until fieldSize) {
      if (gameField(row)(col) == State.Unknown) {
        return false
      }
    }
    true
  }

  private def getConfigurations(segments: List[Int]): List[Array[Int]] = {
    // a single 0 indicates a blank row/col and needs special treatment
    if (segments.length == 1 && segments.head == 0)
    {
      return List(Array.fill(fieldSize)(State.Blank))
    }

    // result list
    val configs = new ListBuffer[Array[Int]]

    val laxity = getLaxity(segments)

    val leftmostPositions = new ListBuffer[Int]
    var pos = 0

    for (segment <- segments) {
      leftmostPositions.append(pos)
      pos += segment + 1
    }

    // start with #segmentCount zeros
    val current = Array.fill[Int](segments.length)(0)
    val config = current.zip(leftmostPositions).map(t => t._1 + t._2).toList
    configs.append(toFieldSetup(segments, config))

    while (current.head < laxity) {
      // find the last index having an increasable value
      var lastIncreasableIdx = current.length - 1
      while (current(lastIncreasableIdx) == laxity) {
        lastIncreasableIdx -= 1
      }

      // increase that value
      current(lastIncreasableIdx) += 1

      // set all following values to the current increased one
      for (s <- lastIncreasableIdx + 1 until current.length) {
        current(s) = current(lastIncreasableIdx)
      }

      // add current to result list
      val config = current.zip(leftmostPositions).map(t => t._1 + t._2).toList
      configs.append(toFieldSetup(segments, config))
    }

    configs.toList
  }

  private def toFieldSetup(segments: List[Int], segmentStarts: List[Int]): Array[Int] = {
    val setup = Array.ofDim[Int](fieldSize)

    for (s <- segments.indices) {
      val start = s match {
        case 0 => 0
        case _ => segmentStarts(s - 1) + segments(s - 1)
      }

      for (blank <- start until segmentStarts(s)) {
        setup(blank) = State.Blank
      }

      for (set <- segmentStarts(s) until segmentStarts(s) + segments(s)) {
        setup(set) = State.Set
      }
    }

    // fill up blanks after last segment
    for (blank <- segmentStarts.last + segments.last until fieldSize) {
      setup(blank) = State.Blank
    }

    setup
  }

  private def removeInvalidConfigs(dimension: Dimension, index: Int, configurations: List[Array[Int]]): List[Array[Int]] = {
    configurations.filter(c => isValid(c, dimension, index))
  }

  private def isValid(fieldSetup: Array[Int], dimension: Dimension, index: Int): Boolean = {
    dimension match {
      case Dimension.Row =>
        for (c <- 0 until fieldSize) {
          if (!(gameField(index)(c) == fieldSetup(c) || gameField(index)(c) == State.Unknown)) {
            return false
          }
        }
        true
      case Dimension.Column =>
        for (r <- 0 until fieldSize) {
          if (!(gameField(r)(index) == fieldSetup(r) || gameField(r)(index) == State.Unknown)) {
            return false
          }
        }
        true
    }
  }
}
