# Mini-projet: présentation Reveal.js + démo Scala.js

Projet de présentation (6 slides principales) pour une session de 10 minutes sur Scala.js, avec une mini app démo exécutable dans le navigateur.

## Prérequis

- Node.js (>= 18)
- npm
- JDK (17+ recommandé)
- sbt

## Structure

- `package.json`: dépendances et scripts front (Vite + Reveal.js)
- `index.html`: entrée des slides Reveal.js
- `slides.md`: contenu des slides (inclut notes speaker)
- `demo.html`: page de démo Scala.js
- `src/main/scala/demo/Main.scala`: logique Scala.js (DOM + compteur)
- `build.sbt`: config Scala.js
- `project/plugins.sbt`: plugin sbt Scala.js

## Installation

```bash
cd /Users/vincenttarrit/Master/MA_AdvProg/ScalaJS/demo-scalajs
npm install
```

## Compiler Scala.js

```bash
sbt fastLinkJS
```

Le JS généré est attendu ici:
`target/scala-3.3.3/scalajs-reveal-demo-fastopt/main.js`

## Lancer les slides

```bash
npm run dev
```

Puis ouvrir:
- `http://localhost:5173/` pour la présentation
- `http://localhost:5173/demo.html` pour la démo seule

## Tester la démo

1. Vérifier que `sbt fastLinkJS` a été exécuté.
2. Ouvrir `http://localhost:5173/demo.html`.
3. Cliquer sur **Incrémenter** et vérifier que le compteur augmente.

## Architecture (court)

- Reveal.js lit `slides.md` depuis `index.html`.
- La slide "Démo live" intègre `demo.html` via iframe.
- `demo.html` charge le fichier JavaScript généré par Scala.js.
- La logique applicative est dans `Main.scala`, compilée via `sbt fastLinkJS`.

## Modifier le contenu

- Slides: éditer `slides.md`
- Démo Scala.js: éditer `src/main/scala/demo/Main.scala`, puis relancer `sbt fastLinkJS`
