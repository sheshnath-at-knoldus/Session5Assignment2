ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "ReadDataFromCsv"
  )
libraryDependencies += "org.apache.commons" % "commons-csv" % "1.10.0"
