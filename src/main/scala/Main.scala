import org.scalajs.dom
import org.scalajs.dom.document

object Dimension extends Enumeration {
  type Dimension = Value
  val Row, Column = Value
}

object State {
  val Unknown = 0
  val Blank = -1
  val Set = 1
}

object Gamemode extends Enumeration {
  type Gamemode = Value
  val FiveLife, Hardcore, Default = Value
}

object Main {

  def main(args: Array[String]): Unit = {
    document.addEventListener("contextmenu", {e: dom.MouseEvent =>
      e.preventDefault()
    })

    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      Menus.createMainMenu()
    })
  }
}