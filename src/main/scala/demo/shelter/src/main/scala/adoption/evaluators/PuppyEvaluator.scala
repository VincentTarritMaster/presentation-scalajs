package adoption

import model.Dog

class PuppyEvaluator extends ApplicationEvaluator[Dog] :

  def evaluate(dog: Dog): Boolean =
    dog.age < 1


