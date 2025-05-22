package org.tmt.sample

import caseapp.{CommandName, ExtraName, HelpMessage}

sealed trait SampleAppCommand

object SampleAppCommand {

  @CommandName("start")
  final case class StartOptions(
     @HelpMessage("port of the app")
     @ExtraName("p")
     port: Option[Int]
   ) extends SampleAppCommand

}
