package org.tmt.library

import caseapp.{CommandName, ExtraName, HelpMessage}

sealed trait LibraryAppCommand

object LibraryAppCommand {

  @CommandName("start")
  final case class StartCommand(
      @HelpMessage("port of the app")
      @ExtraName("p")
      port: Option[Int]
  ) extends LibraryAppCommand

}
