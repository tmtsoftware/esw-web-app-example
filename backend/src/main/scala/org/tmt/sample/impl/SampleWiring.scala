package org.tmt.sample.impl

import org.apache.pekko.http.scaladsl.server.Route
import esw.http.template.wiring.ServerWiring
import org.tmt.sample.http.SampleRoute

class SampleWiring(val port: Option[Int]) extends ServerWiring {
  override lazy val actorSystemName: String = "sample-actor-system"

  // #raDecImpl-ref
  lazy val raDecImpl = new RaDecImpl()
  // #raDecImpl-ref

  // #add-route
  import actorRuntime.ec
  override lazy val routes: Route = new SampleRoute(raDecImpl, securityDirectives).route
  // #add-route
}
