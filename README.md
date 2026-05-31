# Mini-projet: présentation Reveal.js + démos Scala.js

Présentation de 10 minutes sur Scala.js avec Reveal.js et 3 démos exécutables dans le navigateur.

## Prérequis

- Node.js (>= 18)
- npm
- JDK (17+ recommandé)
- sbt

## Structure

- `package.json`: scripts front (Vite)
- `index.html`: entrée Reveal.js
- `slides.md`: contenu des slides + notes speaker
- `demo.html`: démo 1 (compteur)
- `shelter-demo.html`: démo 2 (filtres ShelterDogs)
- `adoption-demo.html`: démo 3 (Adoption Engine)
- `src/main/scala/demo/Main.scala`: point d’entrée Scala.js
- `src/main/scala/demo/increment/`: code démo compteur
- `src/main/scala/demo/game/`: code démo state typé
- `src/main/scala/demo/shelter/`: code démos ShelterDogs (loader, model, adoption)
- `build.sbt`, `project/plugins.sbt`: config sbt + Scala.js

## Installation

```bash
cd /Users/vincenttarrit/Master/MA_AdvProg/ScalaJS/demo-scalajs
npm install
```

## Compiler Scala.js

```bash
sbt fastLinkJS
```

## Lancer la présentation

```bash
npm run start:all
```

Puis ouvrir:
- `http://localhost:5173/` (la présentation)

Les iframes se chargent automatiquement dans les slides:
- `/demo.html`
- `/shelter-demo.html`
- `/adoption-demo.html`

## Modifier le contenu

- Slides: `slides.md`
- Démo 1 compteur: `src/main/scala/demo/increment/IncrementDemo.scala`
- Démo 2 filtres + Démo 3 adoption: `src/main/scala/demo/shelter/ShelterDemo.scala`
- Modèles/loader ShelterDogs: `src/main/scala/demo/shelter/src/main/scala/`
