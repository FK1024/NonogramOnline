object RulesText {
  val rules1 = "Your aim in these puzzles is to colour the whole grid into black and white squares. " +
      "Leaving one empty, is equal to marking it white. " +
      "Beside each row of the grid are listed the lengths of the runs of black squares on that row. " +
      "Above each column are listed the lengths of the runs of black squares in that column. " +
      "These numbers tell you the runs of black squares in that row/column. So, if you see '10 1', " +
      "that tells you that there will be a run of exactly 10 black squares, followed by one or more white square, followed by a single black square. " +
      "There may be more white squares before/after this sequence. "

  val rules2 = "Left click on a square to make it black. Right click to mark it white. Click and drag to mark more than one square"

  val rules3 = "To solve a puzzle, one needs to determine which cells will be boxes and which will be empty. " +
      "Determining which cells are to be left empty is as important as determining which to fill. " +
      "Later in the solving process, the spaces help determine where a clue may spread. " +
      "It is also important never to guess. Only cells that can be determined by logic should be filled. " +
      "If guessing, a single error can spread over the entire field and completely ruin the solution."
}
