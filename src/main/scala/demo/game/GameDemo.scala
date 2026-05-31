package demo.game

import org.scalajs.dom
import org.scalajs.dom.document

object GameDemo {
  case class Player(name: String, points: Int)
  case class GameState(players: List[Player])

  enum Action:
    case AddPoint(playerName: String)
    case Reset

  def update(state: GameState, action: Action): GameState =
    action match
      case Action.AddPoint(target) =>
        state.copy(players = state.players.map { p => if p.name == target then p.copy(points = p.points + 1) else p })
      case Action.Reset =>
        state.copy(players = state.players.map(p => p.copy(points = 0)))

  def mount(): Unit = {
    val appRoot = document.getElementById("app")
    if (appRoot == null) return

    val card = document.createElement("section")
    card.setAttribute("style", "margin-top: 28px; padding-top: 16px; border-top: 1px solid #dbe4ff;")

    val title = document.createElement("h2")
    title.textContent = "Démo 2: state typé (case class + enum)"

    val scoreboard = document.createElement("p")
    scoreboard.setAttribute("style", "font-weight: 600;")

    val addAlice = document.createElement("button").asInstanceOf[dom.html.Button]
    addAlice.textContent = "+1 Alice"
    addAlice.setAttribute("style", "margin-right: 8px;")

    val addBob = document.createElement("button").asInstanceOf[dom.html.Button]
    addBob.textContent = "+1 Bob"
    addBob.setAttribute("style", "margin-right: 8px;")

    val reset = document.createElement("button").asInstanceOf[dom.html.Button]
    reset.textContent = "Reset"
    reset.setAttribute("style", "background: #6c7a95;")

    val explanation = document.createElement("p")
    explanation.textContent = "Ici, l'état est une case class, les événements sont un enum, et toute la logique passe par une fonction update."

    var state = GameState(List(Player("Alice", 0), Player("Bob", 0)))

    def render(): Unit =
      scoreboard.textContent = s"Score: ${state.players.map(p => s"${p.name}: ${p.points}").mkString(" | ")}"

    addAlice.addEventListener("click", (_: dom.Event) => { state = update(state, Action.AddPoint("Alice")); render() })
    addBob.addEventListener("click", (_: dom.Event) => { state = update(state, Action.AddPoint("Bob")); render() })
    reset.addEventListener("click", (_: dom.Event) => { state = update(state, Action.Reset); render() })

    render()
    card.appendChild(title)
    card.appendChild(scoreboard)
    card.appendChild(addAlice)
    card.appendChild(addBob)
    card.appendChild(reset)
    card.appendChild(explanation)
    appRoot.appendChild(card)
  }
}
