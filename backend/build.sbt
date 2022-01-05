lazy val `backend` = project
  .in(file("."))
  .aggregate(`ignore`)
  .settings(
    inThisBuild(
      List(
        scalaVersion := "2.13.6",
        version := "0.1.0"
      )
    ),
    name := "backend",
    resolvers += "jitpack" at "https://jitpack.io",
    // #add-db
    libraryDependencies ++= Seq(
      Libs.`esw-http-template-wiring` % "compile->compile;test->test",
      Libs.`csw-database`, // <---
      Libs.`embedded-keycloak` % Test,
      // #add-db
      Libs.`scalatest`                % Test,
      Libs.`akka-http-testkit`        % Test,
      Libs.`mockito`                  % Test,
      Libs.`akka-actor-testkit-typed` % Test,
      Libs.`akka-stream-testkit`      % Test
    ),
    fork := true,
    Test / fork := false
  )

lazy val `ignore` = project.in(file(".ignore"))
