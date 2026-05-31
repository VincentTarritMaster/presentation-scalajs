package policies

import model.Dog

class PuppyPolicy extends AdoptionPolicy[Dog] :

  override def isAllowed(dog: Dog): Boolean =
    dog.age < 1

