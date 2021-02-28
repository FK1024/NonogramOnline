import Enums.GameMode.GameMode

class GameContext {
  var puzzle: Puzzle = _
  var solution: Array[Array[Int]] = _
  var size = 0
  var mode: GameMode = _
  var lives = 0
  var gameOver = false
}
