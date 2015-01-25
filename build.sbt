scalaVersion := "2.10.4"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

// scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8", 
  "com.typesafe.akka" %% "akka-cluster" % "2.3.8"
)

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10+"
