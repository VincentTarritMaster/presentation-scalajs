package adoption

import model.Animal

case class AdoptionApplication[+T <: Animal](
    applicantName: String,
    animal: T
)
