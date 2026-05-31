package adoption

trait ApplicationEvaluator[-T] :

  def evaluate(animal: T): Boolean

