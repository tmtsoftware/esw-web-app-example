name := "backend"

version := "0.0.1"

scalaVersion := "2.13.5"

resolvers += "jitpack" at "https://jitpack.io"
fork := true
run / javaOptions += "-Dcsw-networks.hostname.automatic=on"

libraryDependencies ++= Seq(
  Libs.`esw-http-template-wiring` % "compile->compile;test->test",
  Libs.`csw-database`,
  Libs.`embedded-keycloak`        % Test,
  Libs.`scalatest`                % Test,
  Libs.`akka-http-testkit`        % Test,
  Libs.`mockito-scala`            % Test,
  Libs.`akka-actor-testkit-typed` % Test,
  Libs.`akka-stream-testkit`      % Test,
  Libs.`otj-pg-embedded`          % Test
)

enablePlugins(FlywayPlugin)
// flyway settings
flywayUrl := "jdbc:postgresql://localhost:5432/postgres"
flywayUser := "postgres"
flywayPassword := "postgres"
flywayLocations += "db/migration"
flywayBaselineOnMigrate := true
flywayBaselineVersion := "0"
