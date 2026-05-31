package exploration

import model.*
import DogExplorationService.*

object DogExplorationServiceTest {

  private val puppy =
    Dog(
      id = 1,
      name = Some("Mira"),
      age = 0.5,
      sex = Sex.Female,
      breed = Breed("Beagle Mix", isMix = true),
      physicalTraits = PhysicalTraits(Color.BrownWhite, Coat.Short, Size.Small),
      behaviour = Behaviour(
        likesPeople = Some(true),
        likesChildren = Some(true),
        getAlongMales = Some(true),
        getAlongFemales = Some(true),
        getAlongCats = None
      ),
      neutered = false,
      housebroken = Some(false),
      dateFound = "2020-01-01",
      adoptableFrom = Some("2020-01-02"),
      posted = "2020-01-03",
      keepIn = Some("flat")
    )

  private val adult =
    Dog(
      id = 2,
      name = None,
      age = 4.0,
      sex = Sex.Male,
      breed = Breed("Beagle, Dachshund Mix", isMix = true),
      physicalTraits = PhysicalTraits(Color.Black, Coat.Short, Size.Medium),
      behaviour = Behaviour(
        likesPeople = Some(true),
        likesChildren = Some(false),
        getAlongMales = Some(false),
        getAlongFemales = Some(true),
        getAlongCats = Some(false)
      ),
      neutered = true,
      housebroken = Some(true),
      dateFound = "2020-01-04",
      adoptableFrom = Some("2020-01-05"),
      posted = "2020-01-06",
      keepIn = Some("garden")
    )

  private val senior =
    Dog(
      id = 3,
      name = Some("Nora"),
      age = 8.0,
      sex = Sex.Female,
      breed = Breed("Unknown Mix", isMix = true),
      physicalTraits = PhysicalTraits(Color.White, Coat.Long, Size.Large),
      behaviour = Behaviour(
        likesPeople = None,
        likesChildren = None,
        getAlongMales = None,
        getAlongFemales = None,
        getAlongCats = None
      ),
      neutered = true,
      housebroken = None,
      dateFound = "2020-01-07",
      adoptableFrom = None,
      posted = "2020-01-08",
      keepIn = None
    )

  private val dogs = List(puppy, adult, senior)

  def main(args: Array[String]): Unit = {
    testSummary()
    testFilter()
    testQuery()
    testCountBySize()
    testMostCommonBreeds()
    testNamesWithAges()
    testBehaviourRates()
    testErrors()
    println("DogExplorationServiceTest: all tests passed")
  }

  private def testSummary(): Unit = {
    val summary = summarizeDataset(dogs).toOption.get

    assert(summary.totalDogs == 3)
    assert(approximately(summary.averageAge, 4.1667))
    assert(approximately(summary.namedRatio, 2.0 / 3.0))
    assert(approximately(summary.neuteredRatio, 2.0 / 3.0))
  }

  private def testFilter(): Unit = {
    val result =
      filterDogs(dogs)(sex = Some(Sex.Female), maximumAge = Some(1.0))

    assert(result == List(puppy))
  }

  private def testQuery(): Unit = {
    val result =
      query(dogs)(_.neutered, _.age >= 4.0).toOption.get

    assert(result == List(adult, senior))
  }

  private def testCountBySize(): Unit =
    assert(countBySize(dogs) == Map(Size.Small -> 1, Size.Medium -> 1, Size.Large -> 1))

  private def testMostCommonBreeds(): Unit = {
    val result = mostCommonBreeds(dogs)(2)

    assert(result == List("Beagle" -> 2, "Dachshund" -> 1))
  }

  private def testNamesWithAges(): Unit =
    assert(namesWithAges(dogs) == List("Mira" -> 0.5, "Unknown" -> 4.0, "Nora" -> 8.0))

  private def testBehaviourRates(): Unit = {
    val result = knownPositiveBehaviourRates(dogs)

    assert(approximately(result("likes_people"), 1.0))
    assert(approximately(result("likes_children"), 0.5))
    assert(approximately(result("gets_along_cats"), 0.0))
  }

  private def testErrors(): Unit = {
    assert(summarizeDataset(Nil) == Left(ExplorationError.EmptyDataset))
    assert(query(dogs)() == Left(ExplorationError.EmptyQuery))
  }

  private def approximately(actual: Double, expected: Double): Boolean =
    (actual - expected).abs < 0.0001
}
