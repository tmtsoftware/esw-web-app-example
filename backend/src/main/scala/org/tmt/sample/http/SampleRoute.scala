package org.tmt.sample.http

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
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

  val route: Route = {
    // #add-routes
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
      post {
        securityDirectives.sPost(RealmRolePolicy("Esw-user")) { _ =>
          entity(as[RaDecRequest]) { raDecRequest =>
            complete(raDecService.raDecToString(raDecRequest))
          }
        }
      }
    }
    // #add-secured-route
  }
}
