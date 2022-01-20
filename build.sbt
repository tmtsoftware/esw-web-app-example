import org.tmt.sbt.docs.DocKeys._
import org.tmt.sbt.docs.Settings

ThisBuild / scalaVersion := "2.13.6"
ThisBuild / organizationName := "TMT Org"
ThisBuild / docsRepo := "https://github.com/tmtsoftware/tmtsoftware.github.io.git"
ThisBuild / docsParentDir := "esw-web-app-example"
ThisBuild / gitCurrentRepo := "https://github.com/tmtsoftware/esw-web-app-example"

version := sys.env.getOrElse("JITPACK_VERSION", "0.1.0-SNAPSHOT")

lazy val openSite =
  Def.setting {
    Command.command("openSite") { state =>
      val uri = s"file://${Project.extract(state).get(siteDirectory)}/${docsParentDir.value}/${version.value}/index.html"
      state.log.info(s"Opening browser at $uri ...")
      java.awt.Desktop.getDesktop.browse(new java.net.URI(uri))
      state
    }
  }

/* ================= Root Project ============== */
lazy val `esw-web-app-example` = project
  .in(file("."))
  .enablePlugins(GithubPublishPlugin)
  .aggregate(docs)
  .settings(
    commands += openSite.value,
    Settings.makeSiteMappings(docs)
  )

/* ================= Paradox Docs ============== */
lazy val docs = project
  .enablePlugins(ParadoxMaterialSitePlugin)
