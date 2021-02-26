import org.scalajs.dom
import org.scalajs.dom.document

object Dimension extends Enumeration {
  type Dimension = Value
  val Row, Column = Value
}

object Gamemode extends Enumeration {
  type Gamemode = Value
  val FiveLife, Hardcore, Default = Value
}

object Main {

  def main(args: Array[String]): Unit = {
    var helper = new Helper
    var buttons = new Buttons(helper)
    var menu = new CreateMenus(helper, buttons)
    buttons.menureference = menu

    document.addEventListener("contextmenu", {e: dom.MouseEvent =>
      e.preventDefault()
    })

    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      menu.createMainMenu()
    })
  }
}