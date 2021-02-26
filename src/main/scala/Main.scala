import org.scalajs.dom
import org.scalajs.dom.document

object Dimension extends Enumeration {
  type Dimension = Value
  val Row, Column = Value
}

object Main {

  def main(args: Array[String]): Unit = {
    var helper = new Helper
    var buttons = new Buttons(helper)
    var menu = new CreateMenus(helper, buttons)

    document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
      menu.createMainMenu()
    })
  }
}