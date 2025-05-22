package org.tmt.sample.http

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import org.tmt.sample.core.models.RaDecRequest
import org.tmt.sample.service.RaDecService

import scala.concurrent.ExecutionContext

//#raDecImpl-ref
class SampleRoute(raDecService: RaDecService, securityDirectives: SecurityDirectives)(implicit
    ec: ExecutionContext
) extends HttpCodecs {
//#raDecImpl-ref

  // #add-routes
  val route: Route = {
    path("raDecValues") {
      post {
        entity(as[RaDecRequest]) { raDecRequest =>
          complete(raDecService.raDecToString(raDecRequest))
        }
      } ~
      get {
        complete(raDecService.getRaDecValues)
      }
    } ~
    // #add-routes
    // #add-secured-route
    path("securedRaDecValues") {
      securityDirectives.sPost(RealmRolePolicy("Esw-user")) { _ =>
        entity(as[RaDecRequest]) { raDecRequest =>
          complete(raDecService.raDecToString(raDecRequest))
        }
      }
    }
    // #add-secured-route
    // #add-routes
  }
  // #add-routes
}
