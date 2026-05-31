package exploration

import model.*

/** Immutable overview of the loaded dog collection.
  *
  * Rates use values between 0.0 and 1.0 so callers can decide whether to print
  * them as percentages, ratios, or raw values.
  */
case class DogDatasetSummary(
    totalDogs: Int,
    averageAge: Double,
    namedRatio: Double,
    neuteredRatio: Double
)

/** Returned when an exploration service cannot produce a meaningful result. */
enum ExplorationError(message: String) extends RuntimeException(message) {
  case EmptyDataset extends ExplorationError("No dogs were loaded.")
  case EmptyQuery extends ExplorationError("At least one query predicate is required.")
}

object DogExplorationService {

  type DogPredicate = Dog => Boolean

  /** A context abstraction keeps ranking rules injectable without mutating state. */
  given countDescending: Ordering[(String, Int)] =
    Ordering.fromLessThan {
      case ((leftName, leftCount), (rightName, rightCount)) =>
        leftCount > rightCount ||
          (leftCount == rightCount && leftName < rightName)
    }

  /** Generic guard for services where an empty dataset would hide a data problem.
    *
    * `onEmpty` is call-by-name so the error value is only built on the failing
    * path. The higher-order `explore` function keeps the empty-list handling
    * reusable and composable across services.
    */
  private def withLoadedDogs[A](dogs: List[Dog])(
      onEmpty: => ExplorationError
  )(explore: List[Dog] => A): Either[ExplorationError, A] =
    dogs match
      case Nil    => Left(onEmpty)
      case loaded => Right(explore(loaded))

  /** Aggregate service: count dogs and summarize age/name/neutering coverage.
    *
    * Uses `foldLeft` instead of mutable counters. The anonymous accumulator
    * function makes the aggregation explicit while preserving immutability.
    */
  def summarizeDataset(
      dogs: List[Dog]
  ): Either[ExplorationError, DogDatasetSummary] =
    withLoadedDogs(dogs)(ExplorationError.EmptyDataset) { loaded =>
      val (count, totalAge, namedCount, neuteredCount) =
        loaded.foldLeft((0, 0.0, 0, 0)) {
          case ((count, ageSum, named, neutered), dog) =>
            (
              count + 1,
              ageSum + dog.age,
              named + dog.name.fold(0)(_ => 1),
              neutered + (if dog.neutered then 1 else 0)
            )
        }

      DogDatasetSummary(
        totalDogs = count,
        averageAge = totalAge / count,
        namedRatio = namedCount.toDouble / count,
        neuteredRatio = neuteredCount.toDouble / count
      )
    }

  /** Filter/query service using parameter groups and a composable predicate.
    *
    * Optional parameters let callers choose any combination of constraints. The
    * local `matches` value is a pure anonymous function consumed by `filter`.
    */
  def filterDogs(
      dogs: List[Dog]
  )(
      sex: Option[Sex] = None,
      size: Option[Size] = None,
      minimumAge: Option[Double] = None,
      maximumAge: Option[Double] = None,
      onlyNeutered: Boolean = false
  ): List[Dog] = {
    val matches: DogPredicate = dog =>
      sex.forall(_ == dog.sex) &&
        size.forall(_ == dog.physicalTraits.size) &&
        minimumAge.forall(_ <= dog.age) &&
        maximumAge.forall(dog.age <= _) &&
        (!onlyNeutered || dog.neutered)

    dogs.filter(matches)
  }

  /** Query service: combine any number of caller-provided predicates.
    *
    * Uses `reduce` to merge predicates into one function. The `match` handles
    * the invalid empty-query case with a typed error instead of throwing.
    */
  def query(
      dogs: List[Dog]
  )(predicates: DogPredicate*): Either[ExplorationError, List[Dog]] =
    predicates.toList match
      case Nil => Left(ExplorationError.EmptyQuery)
      case first :: rest =>
        val combined =
          (first :: rest).reduce((left, right) => dog => left(dog) && right(dog))
        Right(dogs.filter(combined))

  /** Grouping service: count how many dogs belong to each size category.
    *
    * `foldLeft` is used as a functional alternative to updating a mutable map.
    */
  def countBySize(dogs: List[Dog]): Map[Size, Int] =
    dogs.foldLeft(Map.empty[Size, Int])((counts, dog) =>
      counts.updatedWith(dog.physicalTraits.size) {
        case Some(value) => Some(value + 1)
        case None        => Some(1)
      }
    )

  /** Breed-ranking service: split composite breed names and rank base breeds.
    *
    * `flatMap` turns a dog list into a breed-token stream, then
    * `groupMapReduce` counts tokens without mutable state. Mixed/compound breed
    * names are normalized because users usually ask about actual breeds, not the
    * exact CSV wording.
    */
  def mostCommonBreeds(
      dogs: List[Dog]
  )(limit: Int)(using ordering: Ordering[(String, Int)]): List[(String, Int)] =
    dogs
      .flatMap(dog =>
        dog.breed.name
          .replace(" Mix", "")
          .split(",")
          .toList
          .map(_.trim)
          .filter(_.nonEmpty)
      )
      .groupMapReduce(identity)(_ => 1)(_ + _)
      .toList
      .sorted
      .take(limit.max(0))

  /** Pairing service: prepare compact `(name, age)` rows for presentation.
    *
    * `zip` deliberately joins separately mapped lists to demonstrate positional
    * pairing while keeping missing names readable.
    */
  def namesWithAges(dogs: List[Dog]): List[(String, Double)] =
    dogs.map(_.name.getOrElse("Unknown")).zip(dogs.map(_.age))

  /** Behaviour service: summarize known boolean behaviour fields by label.
    *
    * `map` and `flatten` collect only known answers. Unknown values are excluded
    * from the denominator so the resulting rates describe observed information.
    */
  def knownPositiveBehaviourRates(dogs: List[Dog]): Map[String, Double] = {
    val labelledAnswers: List[(String, Option[Boolean])] =
      dogs.flatMap(dog =>
        List(
          "likes_people" -> dog.behaviour.likesPeople,
          "likes_children" -> dog.behaviour.likesChildren,
          "gets_along_males" -> dog.behaviour.getAlongMales,
          "gets_along_females" -> dog.behaviour.getAlongFemales,
          "gets_along_cats" -> dog.behaviour.getAlongCats
        )
      )

    labelledAnswers
      .groupMap(_._1)(_._2)
      .view
      .mapValues(options =>
        options.flatten match
          case Nil => 0.0
          case known =>
            known.count(_ == true).toDouble / known.size
      )
      .toMap
  }
}
