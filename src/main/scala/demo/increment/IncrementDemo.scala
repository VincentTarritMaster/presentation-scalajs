package demo.increment

import org.scalajs.dom
import org.scalajs.dom.document

object IncrementDemo {
  def mount(): Unit = {
    val appRoot = document.getElementById("app")
    if (appRoot == null) return

    var count = 0

    val title = document.createElement("h1")
    title.textContent = "Hello from Scala.js"

    val counter = document.createElement("p")
    counter.id = "counter"
    counter.textContent = s"Compteur: $count"

    val button = document.createElement("button").asInstanceOf[dom.html.Button]
    button.textContent = "Incrémenter"

    val explanation = document.createElement("p")
    explanation.textContent =
      "La logique de ce compteur est écrite en Scala puis compilée en JavaScript avec Scala.js."

    button.addEventListener("click", (_: dom.Event) => {
      count += 1
      counter.textContent = s"Compteur: $count"
    })

    appRoot.appendChild(title)
    appRoot.appendChild(counter)
    appRoot.appendChild(button)
    appRoot.appendChild(explanation)
  }
}
