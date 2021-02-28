import puzzles.size_10x10.MrKrabs
import puzzles.size_15x15.{House, OMF}
import puzzles.size_20x20.SailingShip
import puzzles.size_5x5.Heart

object PuzzleList {
  val size5 = List(Heart.puzzle)
  val size10 = List(MrKrabs.puzzle)
  val size15 = List(House.puzzle, OMF.puzzle)
  val size20 = List(SailingShip.puzzle)
}
