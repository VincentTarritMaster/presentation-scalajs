# Une forêt sans arbres.

Note:
Script oral (10 sec):
Première image absurde pour capter l'attention.
---
# Un château de sable sans sable.

Note:
Script oral (10 sec):
On renforce l'idée d'impossible.
---
# Du JavaScript sans JavaScript.

Note:
Script oral (10 sec):
Transition vers le sujet technique.
---
# Scala.js.

Note:
Script oral (15 sec):
Révélation: ce qui semblait absurde devient concret grâce à Scala.js.
---
## C'est quoi Scala.js ?

<ul>
  <li class="fragment">Un compilateur <strong>Scala vers JavaScript</strong>.</li>
  <li class="fragment">Le code peut tourner dans le <strong>navigateur</strong> ou dans <strong>Node.js</strong>.</li>
  <li class="fragment">Idée clé: conserver le <strong>typage</strong> et l'écosystème Scala côté front.</li>
</ul>

Note:
Script oral (1 min 15):
Scala.js prend du code Scala standard et produit du JavaScript exécutable. Ce n'est pas une réécriture manuelle, c'est un pipeline de compilation complet.
---
## Pourquoi l'utiliser ?

<ul>
  <li class="fragment"><strong>Typage fort</strong>: plus de bugs attrapés à la compilation.</li>
  <li class="fragment"><strong>Partage de code</strong> backend/frontend.</li>
  <li class="fragment"><strong>Refactoring plus sûr</strong>.</li>
  <li class="fragment">Très utile pour des équipes <strong>déjà Scala</strong>.</li>
</ul>

Note:
Script oral (1 min 15):
Quand on fait déjà du Scala côté backend, Scala.js réduit la friction et garde la cohérence du domaine.
---
## Comment ça marche ?

Scala source → Scala.js compiler/linker → JavaScript → navigateur

```scala
import org.scalajs.dom

@main def run(): Unit =
  dom.document.getElementById("app").textContent = "Hello from Scala.js"
```

Note:
Script oral (1 min 15):
En dev: fastLinkJS pour itérer vite. En prod: fullLinkJS pour optimiser davantage.
---
## Pipeline de compilation Scala.js

```text
1) Code Scala (.scala)
   ↓ compilation Scala.js
2) IR Scala.js (.sjsir)
   ↓ linker (fastLinkJS en dev)
3) Bundle JavaScript (main.js)
   ↓
4) Exécution dans le navigateur
```

```bash
sbt fastLinkJS
# => target/scala-3.3.3/scalajs-reveal-demo-fastopt/main.js
```

Note:
Script oral (50 sec):
Point clé: Scala.js ne transpile pas ligne à ligne. Il passe par un IR intermédiaire, puis le linker construit le JavaScript final. fastLinkJS privilégie la vitesse pour le dev; fullLinkJS privilégie l'optimisation pour la prod.
---
## Démo 1: compteur
<iframe class="demo-frame" src="/demo.html" title="Démo Scala.js compteur"></iframe>

Note:
Script oral (40 sec):
Démo de base: état local, clic, rendu.
---
## Démo 2: ShelterDogs filtrable

<iframe class="demo-frame" src="/shelter-demo.html" title="Démo filtres ShelterDogs"></iframe>

Note:
Script oral (1 min):
On charge le CSV réel, puis on filtre en live avec des fonctions Scala typées.
---
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

Note:
Script oral (50 sec):
Ici, c'est la version claire: loader + filtres métier.
--V--
## Code (détail CSV)

```scala
private def parseCsvLine(line: String): Array[String] =
  val out = scala.collection.mutable.ArrayBuffer.empty[String]
  val current = new StringBuilder
  var inQuotes = false
  var i = 0
  while i < line.length do
    val ch = line.charAt(i)
    if ch == '"' then
      if inQuotes && i + 1 < line.length && line.charAt(i + 1) == '"' then
        current.append('"'); i += 1
      else inQuotes = !inQuotes
    else if ch == ',' && !inQuotes then
      out += current.toString; current.clear()
    else current.append(ch)
    i += 1
  out += current.toString
  out.toArray
```

Note:
Slide verticale: parsing robuste des champs quotés.
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

Note:
Slide verticale: ici on voit le coût JS "stringly-typed" et les gardes runtime supplémentaires. En Scala, Option et enums verrouillent déjà ces cas à la compilation.
---
## Démo 3: Adoption Engine

<iframe class="demo-frame" src="/adoption-demo.html" title="Démo Adoption Engine"></iframe>

Note:
Script oral (1 min 10):
Ici on démontre une décision métier: on choisit un chien, une règle d'évaluation, puis on calcule Approved/Rejected.
---
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

Note:
Script oral (1 min):
C'est le point clé de la démo: le front déclenche une vraie logique de domaine, pas juste du filtrage UI.
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

Note:
Slide verticale: règles métier séparées, simples à tester.
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

Note:
Slide verticale: avec Scala, le type union `Approved | Rejected` impose un traitement exhaustif; en JS, on sécurise au runtime.
---
## Limites + conclusion

<ul>
  <li class="fragment">Courbe d'apprentissage et tooling.</li>
  <li class="fragment">Écosystème front moins standard que TypeScript/React.</li>
  <li class="fragment">Très fort quand on veut partager du code Scala.</li>
</ul>

> <span class="fragment">Scala.js n'essaie pas de remplacer JavaScript partout : il donne une option solide aux équipes Scala.</span>

Note:
Script oral (50 sec):
Scala.js est surtout un choix stratégique pour les équipes full-stack Scala.
--V--
## Bonus: questions / ressources

- Scala.js docs: https://www.scala-js.org/
- Reveal.js docs: https://revealjs.com/
- Commande démo: `sbt fastLinkJS`
