package policies

// Contravariant type
trait AdoptionPolicy[-T] :

  def isAllowed(animal: T): Boolean


