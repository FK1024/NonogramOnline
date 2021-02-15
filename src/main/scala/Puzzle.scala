class Puzzle(rowSegs: List[List[Int]], colSegs: List[List[Int]]) {
  val rowSegments: List[List[Int]] = rowSegs
  val colSegments: List[List[Int]] = colSegs

  //---------------------------------------
  private def getColsOrRowsSize(L: List[List[Int]]): Int ={
    var counter = 0
    for(l <- L) {
      if(counter < l.length) {
        counter = l.length
      }
    }
    return counter
  }

  def getRowSegmentSize(): Int = {
    return rowSegments.length
  }

  def getColSegmentSize(): Int = {
    return colSegments.length
  }

  def getRowSize(): Int = {
    getColsOrRowsSize(rowSegments)
  }

  def getColSize(): Int = {
    getColsOrRowsSize(colSegments)
  }
}
