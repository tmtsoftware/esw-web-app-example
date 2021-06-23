package org.tmt.library

import akka.http.scaladsl.server.Route
import csw.database.DatabaseServiceFactory
import esw.http.template.wiring.ServerWiring
import org.jooq.DSLContext
import org.tmt.library.impl.LibraryServiceImpl
import org.tmt.library.http.LibraryRoutes

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class LibraryWiring(val port: Option[Int] = None) extends ServerWiring {

  override lazy val actorSystemName: String = "library-actor-system"
  import actorRuntime.ec

  private lazy val databaseServiceFactory = new DatabaseServiceFactory(actorRuntime.typedSystem)
  private val dbName                      = settings.config.getString("dbName")
  private val dbUsernameHolder            = settings.config.getString("dbUsernameHolder")
  private val dbPasswordHolder            = settings.config.getString("dbPasswordHolder")
  private lazy val dslContext: DSLContext =
    Await.result(
      databaseServiceFactory.makeDsl(cswServices.locationService, dbName, dbUsernameHolder, dbPasswordHolder),
      10.seconds
    )

  logger.info(s"Successfully connected to the database $dbName and stared the server")
  private lazy val libraryImpl    = new LibraryServiceImpl(dslContext)
  override lazy val routes: Route = new LibraryRoutes(libraryImpl).routes
}
