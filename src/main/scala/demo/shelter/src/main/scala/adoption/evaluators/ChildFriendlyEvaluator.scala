package adoption

import model.Dog

class ChildFriendlyEvaluator extends ApplicationEvaluator[Dog] :

  def evaluate(dog: Dog): Boolean =
    dog.behaviour.likesChildren.getOrElse(false)

