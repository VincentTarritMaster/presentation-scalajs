package adoption

import model.Dog

class SeniorEvaluator extends ApplicationEvaluator[Dog] :

  def evaluate(dog: Dog): Boolean =
    dog.age > 6


