import sbt._

object Libs {
  val `esw-http-template-wiring` = "com.github.tmtsoftware.esw" %% "esw-http-template-wiring" % "1.0.2"
  // #add-db
  val `csw-database` = "com.github.tmtsoftware.csw" %% "csw-database" % "6.0.0"
  // #add-db

  //testing
  val `pekko-http-testkit` = "org.apache.pekko" %% "pekko-http-testkit" % "1.1.0"
  val `pekko-actor-testkit-typed` = "org.apache.pekko" %% "pekko-actor-testkit-typed" % "1.1.3"
  val `pekko-stream-testkit` = "org.apache.pekko" %% "pekko-stream-testkit" % "1.1.3"

  val `embedded-keycloak` = "com.github.tmtsoftware.embedded-keycloak" %% "embedded-keycloak" % "0.7.4"
  val `mockito` = "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0"
  val `scalatest` = "org.scalatest" %% "scalatest" % "3.2.19"
}
