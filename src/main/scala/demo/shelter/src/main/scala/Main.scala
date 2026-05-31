import loader.DogCSVLoader
import model.*
import policies.*
import adoption.*

//object Main extends App {
@main def main =

  /* val dogs = DogCSVLoader.loadDogs("data/07-ShelterDogs.csv")

  println(s"Loaded ${dogs.size} dogs")

  dogs.take(5).foreach(println) */

  /*  val dogsNeutered = DogCSVLoader.loadDogsFiltered(
    "data/07-ShelterDogs.csv",
    _.neutered == YesNo.Yes
  )*/

  /* val dogs = DogCSVLoader.loadDogsFiltered(
    "data/07-ShelterDogs.csv",
    _.sex == Sex.Female
  ) */

  /* val dogs = DogCSVLoader.loadDogsFiltered(
    "data/07-ShelterDogs.csv",
    _.adoptableFrom.exists(_ <= "2019-12-08")
  )
  println(s"Loaded ${dogs.size} dogs") */
  /*
  val puppyPolicy = new PuppyPolicy()

  val puppies = DogCSVLoader.loadDogsFiltered(
    "data/07-ShelterDogs.csv",
    puppyPolicy.isAllowed
  )

  println(s"Loaded ${puppies.size} puppies") */

  val dog: Dog = DogCSVLoader.loadDogs("data/07-ShelterDogs.csv").head

  println("Evaluating adoption application for:")
  println(dog)

  val application =
    AdoptionApplication("Alice", dog)

  // Create evaluator
  val evaluator = new ChildFriendlyEvaluator()

  // Create engine
  val engine = new AdoptionEngine[Dog]()

  val decision = engine.decide(application, evaluator)

  println(decision)


