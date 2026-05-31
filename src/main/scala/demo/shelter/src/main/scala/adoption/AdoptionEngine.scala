package adoption

import model.Animal

class AdoptionEngine[T <: Animal] :

  def decide(
      application: AdoptionApplication[T],
      evaluator: ApplicationEvaluator[T]
  ): AdoptionDecision =
    if (evaluator.evaluate(application.animal)) then
      Approved("Application accepted")
    else 
      Rejected("Animal does not match criteria")
    


