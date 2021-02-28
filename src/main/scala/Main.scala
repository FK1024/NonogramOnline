import org.scalajs.dom
import org.scalajs.dom.document

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