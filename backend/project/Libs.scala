import sbt._

object Libs {
  val `esw-http-template-wiring` = "com.github.tmtsoftware.esw" %% "esw-http-template-wiring" % "0.3.0"
  // #add-db
  val `csw-database` = "com.github.tmtsoftware.csw" %% "csw-database" % "4.0.0"
  // #add-db

  //testing
  val `akka-http-testkit`        = "com.typesafe.akka"                        %% "akka-http-testkit"        % "10.2.4"
  val `akka-actor-testkit-typed` = "com.typesafe.akka"                        %% "akka-actor-testkit-typed" % "2.6.15"
  val `akka-stream-testkit`      = "com.typesafe.akka"                        %% "akka-stream-testkit"      % "2.6.15"
  val `embedded-keycloak`        = "com.github.tmtsoftware.embedded-keycloak" %% "embedded-keycloak"        % "0.4.0"
  val `mockito`                  = "org.scalatestplus"                        %% "mockito-3-4"              % "3.2.10.0"
  val `scalatest`                = "org.scalatest"                            %% "scalatest"                % "3.2.2"
}
