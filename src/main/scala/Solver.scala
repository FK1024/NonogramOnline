import Dimension.{Column, Dimension, Row}
import scala.collection.mutable.ListBuffer

object Solver {
  private var fieldSize = 0
  private var gameField: Array[Array[Int]] = Array[Array[Int]]()
  private var wrongField: Array[Array[Int]] = Array[Array[Int]]()

  def solve(puzzle: Puzzle): Array[Array[Int]] = {
    // initialize
    fieldSize = puzzle.rowSegments.length
    gameField = Array.ofDim[Int](fieldSize, fieldSize)
    wrongField = Array.ofDim[Int](fieldSize, fieldSize)

    // check for overfilled rows/cols
    for (i <- 0 until fieldSize) {
      if (getLaxity(puzzle.rowSegments(i)) < 0) {
        throw new Exception(s"the segments of row $i are too long to fit in the game field")
      }
      if (getLaxity(puzzle.colSegments(i)) < 0) {
        throw new Exception(s"the segments of column $i are too long to fit in the game field")
      }
    }

    var openRows = new ListBuffer[Int]
    var openCols = new ListBuffer[Int]
    var openSet = new ListBuffer[Int]
    var solvedCell = true
    val solvedItems = new ListBuffer[Int]

    // handle rows/cols containing just a 0 first
    for (i <- 0 until fieldSize) {
      if (puzzle.rowSegments(i).length == 1 && puzzle.rowSegments(i).head == 0) {
        for (j <- 0 until fieldSize) {
          gameField(i)(j) = State.Blank
        }
      } else {
        openRows.append(i)
      }

      if (puzzle.colSegments(i).length == 1 && puzzle.colSegments(i).head == 0) {
        for (j <- 0 until fieldSize) {
          gameField(j)(i) = State.Blank
        }
      } else {
        openCols.append(i)
      }
    }

    while (openRows.nonEmpty && openCols.nonEmpty && solvedCell) {
      solvedCell = false
      for (dim <- Dimension.values) {
        solvedItems.clear()

        openSet = dim match {
          case Row => openRows
          case Column => openCols
        }

        for (index <- openSet) {
          val validConfigs = getValidConfigurations(puzzle, dim, index)
          if (validConfigs.isEmpty) {
            throw new Exception("The puzzle was not solvable")
          }
          val transposed = validConfigs.transpose // TransposeConfig(validConfigs)
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
      throw new Exception("The gameField was not solvable")
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

  private def getValidConfigurations(puzzle: Puzzle, dimension: Dimension, index: Int): List[Array[Int]] = {
    val validConfigs = new ListBuffer[Array[Int]]

    val segments = dimension match {
      case Dimension.Row => puzzle.rowSegments(index)
      case Dimension.Column => puzzle.colSegments(index)
    }

    val configurations = getConfigurations(segments, getLaxity(segments))

    for (configuration <- configurations) {
      val fieldSetup = toFieldSetup(segments, configuration)
      if (isValid(fieldSetup, dimension, index)) {
        validConfigs.append(fieldSetup)
      }
    }

    validConfigs.toList
  }

  private def getConfigurations(segments: List[Int], laxity: Int): List[List[Int]] = {
    // result list
    val configs = new ListBuffer[List[Int]]

    val leftmostPositions = new ListBuffer[Int]
    var pos = 0

    for (segment <- segments) {
      leftmostPositions.append(pos)
      pos += segment + 1
    }

    // start with #segmentCount zeros
    val current = Array.fill[Int](segments.length)(0)
    configs.append(current.zip(leftmostPositions).map(t => t._1 + t._2).toList)

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
      configs.append(current.zip(leftmostPositions).map(t => t._1 + t._2).toList)
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

  def submitSolution(submission: Array[Array[Int]]): Boolean = {
    for(y <- gameField.indices) {
      for(x <- gameField.indices) {
        if(gameField(y)(x) == State.Set && submission(y+1)(x+1) != State.Set) return false
        if(gameField(y)(x) == State.Blank && submission(y+1)(x+1) == State.Set) return false
      }
    }
    true
  }

  def checkPosition(submission: Array[Array[Int]]): (Boolean, Array[Array[Int]], Int) = {
    var check = true
    var count = 0

    for(y <- wrongField.indices) {
      for(x <- wrongField.indices) {
        wrongField(y)(x) = 0
      }
    }

    for(y <- gameField.indices) {
      for(x <- gameField.indices) {
        if(submission(y+1)(x+1) == State.Set && gameField(y)(x) != State.Set) {
          check = false
          wrongField(y)(x) = 1
          count += 1
        }
      }
    }
    return (check, wrongField, count)
  }
}
