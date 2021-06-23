package org.tmt.library

import caseapp.core.RemainingArgs
import csw.location.api.models.Metadata
import csw.logging.client.scaladsl.LoggingSystemFactory
import esw.http.template.wiring.ServerApp
import org.tmt.library.LibraryAppCommand.StartCommand

object LibraryApp extends ServerApp[LibraryAppCommand] {
  override def appName: String = getClass.getSimpleName.dropRight(1)

  override def run(command: LibraryAppCommand, remainingArgs: RemainingArgs): Unit =
    command match {
      case StartCommand(port) =>
        val wiring = new LibraryWiring(port)
        start(wiring, Metadata.empty)
    }
}
