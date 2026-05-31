package adoption

// Union type for possible adoption outcomes
type AdoptionDecision = Approved | Rejected

case class Approved(message: String)

case class Rejected(reason: String)
