package model

case class Behaviour(
    likesPeople: Option[Boolean],
    likesChildren: Option[Boolean],
    getAlongMales: Option[Boolean],
    getAlongFemales: Option[Boolean],
    getAlongCats: Option[Boolean]
)
