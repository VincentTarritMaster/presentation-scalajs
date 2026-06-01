# Une forêt sans arbres.

---
# Un château de sable sans sable.

---
# Du JavaScript sans JavaScript.

---
# Scala.js

---
## C'est quoi Scala.js ?

<ul>
  <li class="fragment">Un compilateur <strong>Scala vers JavaScript</strong>.</li>
  <li class="fragment">Le code peut tourner dans le <strong>navigateur</strong> ou dans <strong>Node.js</strong>.</li>
  <li class="fragment">Idée clé: conserver le <strong>typage</strong> et l'écosystème Scala côté JS.</li>
</ul>

---
## Pourquoi l'utiliser ?

<ul>
  <li class="fragment"><strong>Typage fort</strong>: plus de bugs attrapés à la compilation.</li>
  <li class="fragment"><strong>Partage de code</strong> backend/frontend.</li>
  <li class="fragment"><strong>Refactoring plus sûr</strong>.</li>
  <li class="fragment">Très utile pour des équipes <strong>déjà Scala</strong>.</li>
</ul>

---
## Comment ça marche ?

Scala source → Scala.js compiler/linker → JavaScript → navigateur

```scala
import org.scalajs.dom

@main def run(): Unit =
  dom.document.getElementById("app").textContent = "Hello from Scala.js"
```

---
## Pipeline de compilation Scala.js

<table style="width:100%; border-collapse: collapse; font-size: 0.82em;">
  <thead>
    <tr>
      <th style="text-align:left; padding:8px 12px;">Scala "normal" (JVM)</th>
      <th style="text-align:left; padding:8px 12px; border-left: 3px solid #7f8fb3;">Scala.js</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="vertical-align:top; padding:8px 12px;">
        <span class="fragment" data-fragment-index="1"><strong style="color:#4da3ff;">Action 1</strong>: écrire du Scala (<strong style="color:#57d18d;">.scala</strong>)</span><br/>
        <span class="fragment" data-fragment-index="2"><strong style="color:#4da3ff;">Action 2</strong>: compiler avec <strong style="color:#57d18d;">scalac</strong></span><br/>
        <span class="fragment" data-fragment-index="3"><strong style="color:#4da3ff;">Action 3</strong>: produire des <strong style="color:#57d18d;">.class</strong> (bytecode JVM)</span><br/>
        <span class="fragment" data-fragment-index="4"><strong style="color:#4da3ff;">Action 4</strong>: exécuter sur la JVM (<strong style="color:#57d18d;">sbt run</strong>)</span>
      </td>
      <td style="vertical-align:top; padding:8px 12px; border-left: 3px solid #7f8fb3;">
        <span class="fragment" data-fragment-index="1"><strong style="color:#ff6b6b;">Action 1</strong>: écrire du Scala (<strong style="color:#57d18d;">.scala</strong>)</span><br/>
        <span class="fragment" data-fragment-index="2"><strong style="color:#ff6b6b;">Action 2</strong>: compiler vers IR Scala.js (<strong style="color:#57d18d;">.sjsir</strong>)</span><br/>
        <span class="fragment" data-fragment-index="3"><strong style="color:#ff6b6b;">Action 3</strong>: linker avec <strong style="color:#57d18d;">fastLinkJS</strong></span><br/>
        <span class="fragment" data-fragment-index="4"><strong style="color:#ff6b6b;">Action 4</strong>: générer <strong style="color:#57d18d;">main.js</strong> pour le navigateur</span>
      </td>
    </tr>
  </tbody>
</table>

---
## Démo 1: compteur
<iframe class="demo-frame" src="demo.html" title="Démo Scala.js compteur"></iframe>

--V--
## Code compteur (rapide)

```scala
var count = 0
counter.textContent = s"Compteur: $count"

button.addEventListener("click", (_: dom.Event) => {
  count += 1
  counter.textContent = s"Compteur: $count"
})
```

<ul>
  <li class="fragment">`count` = état local en Scala.</li>
  <li class="fragment">Le clic déclenche la mise à jour.</li>
  <li class="fragment">Le DOM est mis à jour immédiatement.</li>
</ul>

---
## Démo 2: ShelterDogs filtrable

<iframe class="demo-frame" src="shelter-demo.html" title="Démo filtres ShelterDogs"></iframe>

--V--
## Code

```scala
import loader.DogCSVLoader
import model.*

case class ShelterFilters(
  neutered: Option[Boolean],
  breed: Option[String],
  color: Option[Color]
)

def filterDogs(dogs: List[Dog], f: ShelterFilters): List[Dog] =
  dogs.filter(d =>
    f.neutered.forall(_ == d.neutered) &&
    f.breed.forall(_ == d.breed.name) &&
    f.color.forall(_ == d.physicalTraits.color)
  )

val dogs = DogCSVLoader.fromCsvContent(csvText)
```


--V--
## Scala → JavaScript (filtre)

```scala
def filterDogs(dogs: List[Dog], f: ShelterFilters): List[Dog] =
  dogs.filter(d =>
    f.neutered.forall(_ == d.neutered) &&
    f.breed.forall(_ == d.breed.name) &&
    f.color.forall(_ == d.physicalTraits.color)
  )
```

```javascript
function filterDogs(dogs, f) {
  return dogs.filter((d) => {
    const neuteredOk =
      f.neutered == null || f.neutered === d.neutered;

    // JS: attention aux undefined et fautes de clé "breed"/"bredd"
    const breedName = d && d.breed ? d.breed.name : undefined;
    const breedOk =
      f.breed == null || f.breed === breedName;

    // JS: color reste une string libre, pas un enum garanti
    const colorValue =
      d && d.physicalTraits ? d.physicalTraits.color : undefined;
    const colorOk =
      f.color == null || f.color === colorValue;

    return neuteredOk && breedOk && colorOk;
  });
}
```

---
## Démo 3: Adoption Engine

<iframe class="demo-frame" src="adoption-demo.html" title="Démo Adoption Engine"></iframe>

--V--
## Code

```scala
import adoption.*
import model.Dog

val evaluator: ApplicationEvaluator[Dog] = mode match
  case "senior" => new SeniorEvaluator
  case "child"  => new ChildFriendlyEvaluator
  case _         => new PuppyEvaluator

val engine = new AdoptionEngine[Dog]
val app = AdoptionApplication("Presenter", selectedDog)
val decision: AdoptionDecision = engine.decide(app, evaluator)
```

<ul>
  <li class="fragment">Règles encapsulées dans des évaluateurs dédiés.</li>
  <li class="fragment">Décision métier typée: <code>Approved | Rejected</code>.</li>
  <li class="fragment">Même logique réutilisable backend/frontend.</li>
</ul>

--V--
## Code (évaluateurs)

```scala
class PuppyEvaluator extends ApplicationEvaluator[Dog]:
  def evaluate(dog: Dog): Boolean = dog.age < 1

class SeniorEvaluator extends ApplicationEvaluator[Dog]:
  def evaluate(dog: Dog): Boolean = dog.age > 6

class ChildFriendlyEvaluator extends ApplicationEvaluator[Dog]:
  def evaluate(dog: Dog): Boolean =
    dog.behaviour.likesChildren.getOrElse(false)
```

--V--
## Scala → JavaScript (adoption)

```scala
val evaluator: ApplicationEvaluator[Dog] = mode match
  case "senior" => new SeniorEvaluator
  case "child"  => new ChildFriendlyEvaluator
  case _        => new PuppyEvaluator

val engine = new AdoptionEngine[Dog]
val decision = engine.decide(AdoptionApplication("Presenter", dog), evaluator)
```

```javascript
const evaluator =
  mode === "senior" ? new SeniorEvaluator()
  : mode === "child" ? new ChildFriendlyEvaluator()
  : new PuppyEvaluator();

const engine = new AdoptionEngine();
const decision = engine.decide(
  new AdoptionApplication("Presenter", dog),
  evaluator
);

// JS: à traiter manuellement si la forme de "decision" change
if (decision && decision.message) {
  renderApproved(decision.message);
} else if (decision && decision.reason) {
  renderRejected(decision.reason);
} else {
  // fallback runtime, pas détecté à la compilation JS
  renderError("Unexpected decision payload");
}
```

---
## Limites

<ul>
  <li class="fragment">Courbe d'apprentissage et tooling.</li>
  <li class="fragment">Écosystème front moins standard que TypeScript/React.</li>
  <li class="fragment">Très fort quand on veut partager du code Scala.</li>
</ul>

> <span class="fragment">Scala.js n'essaie pas de remplacer JavaScript partout : il donne une option solide aux équipes Scala.</span>

--V--
## Dépendances npm

<ul>
  <li class="fragment">Avec JavaScript, beaucoup de libs npm marchent “direct”.</li>
  <li class="fragment">Avec Scala.js, certaines libs npm demandent une couche d’adaptation.</li>
  <li class="fragment">Cette adaptation ajoute du code à maintenir.</li>
  <li class="fragment">Si la lib npm change, il faut parfois ajuster cette couche manuellement.</li>
</ul>

---
## Exemple de “pont” Scala.js → lib JS

<div style="font-size: 0.8em;">

```scala
@js.native
@JSImport("chart.js/auto", JSImport.Default)
object ChartJS extends js.Object

def drawChart(canvasId: String, labels: List[String], values: List[Double]): Unit = {
  // appel de la lib JS via ce pont
}
```

<ul>
  <li class="fragment">Le “pont” relie le code Scala à une API JavaScript.</li>
  <li class="fragment">C'est utile, mais c'est du code en plus à maintenir.</li>
  <li class="fragment">Si l'API npm change, ce pont peut devoir être adapté.</li>
</ul>

--V--
## Démo 4: pont npm

<iframe class="demo-frame" src="is13-demo.html" title="Démo is-thirteen"></iframe>

--V--
## lib npm `is-thirteen`

```scala
@js.native
trait IsThirteenResult extends js.Object:
  def thirteen(): Boolean = js.native

@js.native
@JSGlobal("isThirteen")
object IsThirteenBridge extends js.Object:
  def apply(value: String): IsThirteenResult = js.native
  def apply(value: Double): IsThirteenResult = js.native

def checkIsThirteen(raw: String): Boolean =
  raw.trim.toDoubleOption match
    case Some(n) => IsThirteenBridge(n).thirteen()
    case None    => IsThirteenBridge(raw.trim).thirteen()
```
---
## Resources

- Scala.js docs: https://www.scala-js.org/
- Reveal.js docs: https://revealjs.com/
---
# Avez-vous des questions ?

Merci.
