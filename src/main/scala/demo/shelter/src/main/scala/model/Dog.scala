package model

case class Dog(
    id: Int,
    name: Option[String],
    age: Double,
    sex: Sex,
    breed: Breed,
    physicalTraits: PhysicalTraits,
    behaviour: Behaviour,
    neutered: Boolean,
    housebroken: Option[Boolean],
    dateFound: String,
    adoptableFrom: Option[String],
    posted: String,
    keepIn: Option[String]
) extends Animal
    with Adoptable :
  override def toString: String = {

    val dogName = name.getOrElse("Unknown")
    val house = housebroken.map(_.toString).getOrElse("Unknown")
    val adoptDate = adoptableFrom.getOrElse("Not specified")
    val location = keepIn.getOrElse("Unknown")

    val likesPeople = behaviour.likesPeople.map(_.toString).getOrElse("Unknown")
    val likesChildren =
      behaviour.likesChildren.map(_.toString).getOrElse("Unknown")
    val getAlongMales =
      behaviour.getAlongMales.map(_.toString).getOrElse("Unknown")
    val getAlongFemales =
      behaviour.getAlongFemales.map(_.toString).getOrElse("Unknown")
    val getAlongCats =
      behaviour.getAlongCats.map(_.toString).getOrElse("Unknown")

    s"""
          Dog ID: $id
          Name: $dogName
          Age: $age years
          Sex: $sex
          Breed: ${breed.name}

          Physical Traits
          Color: ${physicalTraits.color}
          Size: ${physicalTraits.size}
          Coat: ${physicalTraits.coat}

          Health
          Neutered: $neutered
          Housebroken: $house

          Behaviour
          Likes people: $likesPeople
          Likes children: $likesChildren
          Gets along with males: $getAlongMales
          Gets along with females: $getAlongFemales
          Gets along with cats: $getAlongCats

          Shelter Info
          Found on: $dateFound
          Adoptable from: $adoptDate
          Posted: $posted
          Keep in: $location

          --------------------
          """.stripMargin
  }

