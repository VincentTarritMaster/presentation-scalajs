package demo.shelter

import adoption.*
import loader.DogCSVLoader
import model.*
import org.scalajs.dom
import org.scalajs.dom.document
import scala.concurrent.ExecutionContext.Implicits.global

object ShelterDemo {
  case class ShelterFilters(neutered: Option[Boolean], breed: Option[String], color: Option[Color])
  case class ShelterState(filters: ShelterFilters, offset: Int)

  def mount(): Unit = {
    mountFiltersDemo()
    mountAdoptionDemo()
  }

  private def loadDogs(onLoaded: List[Dog] => Unit): Unit =
    dom.fetch("data/07-ShelterDogs.csv")
      .toFuture
      .flatMap(_.text().toFuture)
      .map(DogCSVLoader.fromCsvContent)
      .foreach(onLoaded)

  private def filterDogs(dogs: List[Dog], filters: ShelterFilters): List[Dog] =
    dogs.filter(dog =>
      filters.neutered.forall(_ == dog.neutered) &&
      filters.breed.forall(_ == dog.breed.name) &&
      filters.color.forall(_ == dog.physicalTraits.color)
    )

  private def selectedColor(value: String): Option[Color] =
    value match
      case "black" => Some(Color.Black)
      case "brown" => Some(Color.Brown)
      case "white" => Some(Color.White)
      case "yellowbrown" => Some(Color.YellowBrown)
      case "red" => Some(Color.Red)
      case "blackwhite" => Some(Color.BlackWhite)
      case "graywhite" => Some(Color.GrayWhite)
      case "brownwhite" => Some(Color.BrownWhite)
      case "saddleback" => Some(Color.SaddleBack)
      case "unknown" => Some(Color.Unknown)
      case _ => None

  private def colorToken(c: Color): String = c match
    case Color.Black => "black"
    case Color.Brown => "brown"
    case Color.White => "white"
    case Color.YellowBrown => "yellowbrown"
    case Color.Red => "red"
    case Color.BlackWhite => "blackwhite"
    case Color.GrayWhite => "graywhite"
    case Color.BrownWhite => "brownwhite"
    case Color.SaddleBack => "saddleback"
    case Color.Unknown => "unknown"

  private def mountFiltersDemo(): Unit = {
    val root = document.getElementById("shelter-app")
    if (root == null) return

    val controls = document.createElement("div")
    controls.setAttribute("style", "display:flex; gap:12px; flex-wrap: wrap; margin: 14px 0;")

    def makeSelectLabel(text: String): dom.html.Label =
      val label = document.createElement("label").asInstanceOf[dom.html.Label]
      label.textContent = text
      label.setAttribute("style", "display:flex; flex-direction:column; font-size:14px; gap:6px;")
      label

    def makeSelect(options: List[(String, String)]): dom.html.Select =
      val select = document.createElement("select").asInstanceOf[dom.html.Select]
      select.setAttribute("style", "padding:8px 10px; border-radius:8px; border:1px solid #ccd7ee;")
      options.foreach { case (value, label) =>
        val opt = document.createElement("option").asInstanceOf[dom.html.Option]
        opt.value = value
        opt.textContent = label
        select.appendChild(opt)
      }
      select

    val neuteredSelect = makeSelect(List("all" -> "Castré: Tous", "yes" -> "Castré: Oui", "no" -> "Castré: Non"))
    val breedSelect = makeSelect(List("all" -> "Race: Toutes"))
    val colorSelect = makeSelect(List("all" -> "Couleur: Toutes"))

    val neuteredLabel = makeSelectLabel("Filtre 1")
    val breedLabel = makeSelectLabel("Filtre 2")
    val colorLabel = makeSelectLabel("Filtre 3")
    neuteredLabel.appendChild(neuteredSelect)
    breedLabel.appendChild(breedSelect)
    colorLabel.appendChild(colorSelect)
    controls.appendChild(neuteredLabel)
    controls.appendChild(breedLabel)
    controls.appendChild(colorLabel)

    val status = document.createElement("p")
    status.setAttribute("style", "font-weight:600;")

    val carousel = document.createElement("div")
    carousel.setAttribute("style", "display:flex; align-items:center; gap:10px;")

    val cards = document.createElement("div")
    cards.setAttribute("style", "display:grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap:10px; flex:1;")

    var dogs: List[Dog] = Nil
    var state = ShelterState(ShelterFilters(None, None, None), 0)

    def setOptions(select: dom.html.Select, options: List[(String, String)]): Unit = {
      select.innerHTML = ""
      options.foreach { case (value, label) =>
        val opt = document.createElement("option").asInstanceOf[dom.html.Option]
        opt.value = value
        opt.textContent = label
        select.appendChild(opt)
      }
    }

    def render(): Unit = {
      val filtered = filterDogs(dogs, state.filters)
      val total = filtered.size
      status.textContent = s"${total} chien(s) correspondent aux filtres."

      if (total == 0) state = state.copy(offset = 0)
      else if (state.offset >= total) state = state.copy(offset = 0)

      cards.innerHTML = ""
      val visible =
        if total == 0 then Nil
        else (0 until math.min(3, total)).toList.map(i => filtered((state.offset + i) % total))

      if (visible.isEmpty) {
        val emptyCard = document.createElement("article")
        emptyCard.setAttribute("style", "padding:12px; border:1px dashed #a5b5d6; border-radius:10px;")
        emptyCard.textContent = "Aucun chien pour ces critères."
        cards.appendChild(emptyCard)
      } else {
        visible.foreach { dog =>
          val card = document.createElement("article")
          card.setAttribute("style", "padding:12px; border:1px solid #dbe4ff; border-radius:10px; background:#f8fbff;")
          card.innerHTML =
            s"<strong>${dog.name.getOrElse("Unknown")}</strong><br/>${dog.breed.name}<br/>Couleur: ${dog.physicalTraits.color}<br/>Castré: ${if dog.neutered then "Oui" else "Non"}<br/>Age: ${dog.age} ans"
          cards.appendChild(card)
        }
      }
    }

    neuteredSelect.addEventListener("change", (_: dom.Event) => {
      val neutered = neuteredSelect.value match
        case "yes" => Some(true)
        case "no" => Some(false)
        case _ => None
      state = state.copy(filters = state.filters.copy(neutered = neutered), offset = 0)
      render()
    })

    breedSelect.addEventListener("change", (_: dom.Event) => {
      val breed = if breedSelect.value == "all" then None else Some(breedSelect.value)
      state = state.copy(filters = state.filters.copy(breed = breed), offset = 0)
      render()
    })

    colorSelect.addEventListener("change", (_: dom.Event) => {
      state = state.copy(filters = state.filters.copy(color = selectedColor(colorSelect.value)), offset = 0)
      render()
    })

    carousel.appendChild(cards)
    root.appendChild(controls)
    root.appendChild(status)
    root.appendChild(carousel)

    status.textContent = "Chargement des chiens..."
    loadDogs { loaded =>
      dogs = loaded
      val breeds = dogs.map(_.breed.name).distinct.sorted
      val colors = dogs.map(_.physicalTraits.color).distinct.sortBy(_.toString)
      setOptions(breedSelect, ("all" -> "Race: Toutes") :: breeds.map(b => b -> s"Race: $b"))
      setOptions(colorSelect, ("all" -> "Couleur: Toutes") :: colors.map(c => colorToken(c) -> s"Couleur: ${c.toString}"))
      render()
    }
  }

  private def mountAdoptionDemo(): Unit = {
    val root = document.getElementById("shelter-adoption-app")
    if (root == null) return

    val intro = document.createElement("p")
    intro.textContent = "Choisissez un chien et une règle d'évaluation, puis lancez la décision d'adoption."

    val dogSelect = document.createElement("select").asInstanceOf[dom.html.Select]
    dogSelect.setAttribute("style", "padding:8px 10px; border-radius:8px; border:1px solid #ccd7ee; min-width: 360px;")

    val evaluatorSelect = document.createElement("select").asInstanceOf[dom.html.Select]
    evaluatorSelect.setAttribute("style", "padding:8px 10px; border-radius:8px; border:1px solid #ccd7ee;")
    List(
      "puppy" -> "Règle: Puppy (< 1 an)",
      "senior" -> "Règle: Senior (> 6 ans)",
      "child" -> "Règle: Child Friendly"
    ).foreach { case (v, t) =>
      val opt = document.createElement("option").asInstanceOf[dom.html.Option]
      opt.value = v
      opt.textContent = t
      evaluatorSelect.appendChild(opt)
    }

    val runButton = document.createElement("button").asInstanceOf[dom.html.Button]
    runButton.textContent = "Évaluer"
    runButton.setAttribute("style", "padding:10px 14px; border:0; border-radius:8px; background:#1d5eff; color:white; cursor:pointer;")

    val row = document.createElement("div")
    row.setAttribute("style", "display:flex; gap:10px; flex-wrap:wrap; margin:12px 0;")
    row.appendChild(dogSelect)
    row.appendChild(evaluatorSelect)
    row.appendChild(runButton)

    val result = document.createElement("div")
    result.setAttribute("style", "margin-top:12px; padding:12px; border-radius:10px; border:1px solid #dbe4ff; background:#f8fbff;")
    result.textContent = "En attente d'évaluation..."

    root.appendChild(intro)
    root.appendChild(row)
    root.appendChild(result)

    var dogs: List[Dog] = Nil

    def selectedDog: Option[Dog] =
      dogSelect.value.toIntOption.flatMap(idx => dogs.lift(idx))

    def renderResult(decision: AdoptionDecision, dog: Dog, ruleLabel: String): Unit = {
      val dogLabel = s"${dog.name.getOrElse("Unknown")} (${dog.breed.name}, ${dog.age} ans)"
      decision match
        case Approved(message) =>
          result.setAttribute("style", "margin-top:12px; padding:12px; border-radius:10px; border:1px solid #8fd19e; background:#eaf9ee;")
          result.innerHTML = s"<strong>APPROUVÉ</strong><br/>$message<br/>Règle: $ruleLabel<br/>Chien: $dogLabel"
        case Rejected(reason) =>
          result.setAttribute("style", "margin-top:12px; padding:12px; border-radius:10px; border:1px solid #e1a4a4; background:#fff1f1;")
          result.innerHTML = s"<strong>REFUSÉ</strong><br/>$reason<br/>Règle: $ruleLabel<br/>Chien: $dogLabel"
    }

    runButton.addEventListener("click", (_: dom.Event) => {
      selectedDog.foreach { dog =>
        val evaluatorKey = evaluatorSelect.value
        val evaluator: ApplicationEvaluator[Dog] = evaluatorKey match
          case "senior" => new SeniorEvaluator
          case "child" => new ChildFriendlyEvaluator
          case _ => new PuppyEvaluator

        val ruleLabel = evaluatorSelect.options(evaluatorSelect.selectedIndex).text
        val engine = new AdoptionEngine[Dog]
        val app = AdoptionApplication("Presenter", dog)
        val decision = engine.decide(app, evaluator)
        renderResult(decision, dog, ruleLabel)
      }
    })

    loadDogs { loaded =>
      dogs = loaded
      dogSelect.innerHTML = ""
      dogs.zipWithIndex.take(300).foreach { case (dog, idx) =>
        val opt = document.createElement("option").asInstanceOf[dom.html.Option]
        val name = dog.name.getOrElse("Unknown")
        opt.value = idx.toString
        opt.textContent = s"$name | ${dog.breed.name} | ${dog.age} ans | enfants: ${dog.behaviour.likesChildren.getOrElse(false)}"
        dogSelect.appendChild(opt)
      }
      result.textContent = s"${dogs.size} chiens chargés. Choisis un chien puis clique sur Évaluer."
    }
  }
}
