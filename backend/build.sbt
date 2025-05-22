lazy val `backend` = project
  .in(file("."))
  .aggregate(`ignore`)
  .settings(
    inThisBuild(
      List(
        scalaVersion := "3.6.4",
        version := "0.3.0"
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
      Libs.`scalatest`                 % Test,
      Libs.`pekko-http-testkit`        % Test,
      Libs.`mockito`                   % Test,
      Libs.`pekko-actor-testkit-typed` % Test,
      Libs.`pekko-stream-testkit`      % Test
    ),
    fork := true,
    Test / fork := false
  )

lazy val `ignore` = project.in(file(".ignore"))
