package model

trait Adoptable :
  def dateFound: String  // jpc: should you use a date type?
  def adoptableFrom: Option[String]
