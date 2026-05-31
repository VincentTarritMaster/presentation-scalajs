package loader

import model.*
import scala.io.Source

object DogCSVLoader {

  def loadDogs(path: String): List[Dog] =
    val lines = Source.fromFile(path, "utf-8").getLines().drop(1).toList
    fromLines(lines)

  def loadDogsFiltered(path: String, filter: Dog => Boolean): List[Dog] =
    loadDogs(path).filter(filter)

  // Variante browser/Scala.js: contenu CSV déjà chargé via fetch.
  def fromCsvContent(csv: String): List[Dog] =
    fromLines(csv.split("\\n").toList.drop(1))

  private def fromLines(lines: List[String]): List[Dog] =
    lines.flatMap { rawLine =>
      val c = parseCsvLine(rawLine.stripSuffix("\r"))
      if c.length < 19 then None
      else
        Some(
          Dog(
            id = c(0).toIntOption.getOrElse(0),
            name = opt(c(1)),
            age = c(2).toDoubleOption.getOrElse(0.0),
            sex = parseSex(c(3)),
            breed = Breed(c(4), c(4).contains("Mix")),
            physicalTraits = PhysicalTraits(
              color = parseColor(c(8)),
              coat = parseCoat(c(9)),
              size = parseSize(c(10))
            ),
            neutered = parseYesNo(c(11)).getOrElse(false),
            housebroken = parseYesNo(c(12)),
            behaviour = Behaviour(
              likesPeople = parseYesNo(c(13)),
              likesChildren = parseYesNo(c(14)),
              getAlongMales = parseYesNo(c(15)),
              getAlongFemales = parseYesNo(c(16)),
              getAlongCats = parseYesNo(c(17))
            ),
            dateFound = c(5),
            adoptableFrom = opt(c(6)),
            posted = c(7),
            keepIn = opt(c(18))
          )
        )
    }

  private def opt(s: String): Option[String] =
    if (s.trim.isEmpty) None else Some(s.trim)

  private def parseSex(s: String): Sex =
    if (s.trim.toLowerCase == "male") Sex.Male else Sex.Female

  private def parseYesNo(s: String): Option[Boolean] =
    s.trim.toLowerCase match
      case "yes" => Some(true)
      case "no"  => Some(false)
      case _      => None

  private def parseSize(s: String): Size =
    s.trim.toLowerCase match
      case "small"  => Size.Small
      case "medium" => Size.Medium
      case "large"  => Size.Large
      case _         => Size.Medium

  private def parseCoat(s: String): Coat =
    s.trim.toLowerCase match
      case "short"  => Coat.Short
      case "medium" => Coat.Medium
      case "long"   => Coat.Long
      case _         => Coat.Short

  private def parseColor(s: String): Color =
    s.trim.toLowerCase match
      case "black"           => Color.Black
      case "brown"           => Color.Brown
      case "white"           => Color.White
      case "yellow-brown"    => Color.YellowBrown
      case "red"             => Color.Red
      case "black and white" => Color.BlackWhite
      case "gray and white"  => Color.GrayWhite
      case "brown and white" => Color.BrownWhite
      case "saddle back"     => Color.SaddleBack
      case _                  => Color.Unknown

  // Support des virgules dans les champs quotés.
  private def parseCsvLine(line: String): Array[String] =
    val out = scala.collection.mutable.ArrayBuffer.empty[String]
    val current = new StringBuilder
    var inQuotes = false
    var i = 0
    while i < line.length do
      val ch = line.charAt(i)
      if ch == '"' then
        if inQuotes && i + 1 < line.length && line.charAt(i + 1) == '"' then
          current.append('"')
          i += 1
        else
          inQuotes = !inQuotes
      else if ch == ',' && !inQuotes then
        out += current.toString
        current.clear()
      else
        current.append(ch)
      i += 1
    out += current.toString
    out.toArray
}
