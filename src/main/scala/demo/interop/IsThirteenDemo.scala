package demo.interop

import org.scalajs.dom
import org.scalajs.dom.document
import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

object IsThirteenDemo {
  @js.native
  trait IsThirteenResult extends js.Object {
    def thirteen(): Boolean = js.native
  }

  @js.native
  @JSGlobal("isThirteen")
  object IsThirteenBridge extends js.Object {
    def apply(value: String): IsThirteenResult = js.native
    def apply(value: Double): IsThirteenResult = js.native
  }

  private def checkIsThirteen(raw: String): Boolean = {
    val trimmed = raw.trim
    val result =
      trimmed.toDoubleOption match
        case Some(number) => IsThirteenBridge(number)
        case None         => IsThirteenBridge(trimmed)
    result.thirteen()
  }

  def mount(): Unit = {
    val root = document.getElementById("is13-app")
    if (root == null) return

    val title = document.createElement("h2")
    title.textContent = "Démo pont JS: is-thirteen"

    val row = document.createElement("div")
    row.setAttribute("style", "display:flex; gap:10px; align-items:center; flex-wrap:wrap;")

    val input = document.createElement("input").asInstanceOf[dom.html.Input]
    input.id = "value"
    input.placeholder = "Tape une valeur..."
    input.setAttribute("style", "padding:10px; border-radius:8px; border:1px solid #ccd7ee; min-width:220px;")

    val button = document.createElement("button").asInstanceOf[dom.html.Button]
    button.id = "check"
    button.textContent = "Vérifier"
    button.setAttribute("style", "padding:10px 14px; border:0; border-radius:8px; background:#1d5eff; color:white; cursor:pointer;")

    val result = document.createElement("p")
    result.id = "result"
    result.setAttribute("style", "font-weight:600; margin-top:12px;")
    result.textContent = "Entre une valeur puis clique sur Vérifier"

    button.onclick = (_: dom.MouseEvent) => {
      try
        val is13 = checkIsThirteen(input.value)
        result.textContent =
          if is13 then "Oui, c'est 13 ✅"
          else "Non, ce n'est pas 13 ❌"
      catch
        case t: Throwable =>
          result.textContent =
            s"Erreur: ${t.getClass.getSimpleName}: ${Option(t.getMessage).getOrElse("no message")}"
    }

    row.appendChild(input)
    row.appendChild(button)
    root.appendChild(title)
    root.appendChild(row)
    root.appendChild(result)
  }
}
