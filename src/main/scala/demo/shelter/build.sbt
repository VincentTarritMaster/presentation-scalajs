name := "ShelterDogs"

version := "0.1"

scalaVersion := "3.3.1"

Test / test := (Test / runMain).toTask(" exploration.DogExplorationServiceTest").value
