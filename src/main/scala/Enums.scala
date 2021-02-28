object Enums {
  object Dimension extends Enumeration {
    type Dimension = Value
    val Row, Column = Value
  }

  object State {
    val Unknown = 0
    val Blank = -1
    val Set = 1
  }

  object GameMode extends Enumeration {
    type GameMode = Value
    val FiveLives, Hardcore, Default = Value
  }
}
