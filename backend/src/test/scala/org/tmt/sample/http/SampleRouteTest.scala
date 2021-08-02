package org.tmt.sample.http

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.BasicDirectives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import csw.aas.http.AuthorizationPolicy.RealmRolePolicy
import csw.aas.http.SecurityDirectives
import csw.location.api.models.ComponentType.Service
import csw.location.api.models.Connection.HttpConnection
import csw.location.api.models._
import csw.prefix.models.Prefix
import io.bullet.borer.compat.AkkaHttpCompat
import msocket.security.models.AccessToken
import org.mockito.MockitoSugar.{mock, reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.wordspec.AnyWordSpec
import org.tmt.sample.TestHelper
import org.tmt.sample.core.models.{RaDecRequest, RaDecResponse}
import org.tmt.sample.impl.RaDecImpl

import scala.concurrent.Future

class SampleRouteTest extends AnyWordSpec with ScalatestRouteTest with AkkaHttpCompat with BeforeAndAfterEach with HttpCodecs {

  // #add-mock
  private val service: RaDecImpl = mock[RaDecImpl]
  // #add-mock
  private val securityDirectives: SecurityDirectives = mock[SecurityDirectives]
  private val token: AccessToken                     = mock[AccessToken]
  private val accessTokenDirective                   = BasicDirectives.extract(_ => token)

  // #add-mock-dep
  private val route: Route                  = new SampleRoute(service, securityDirectives).route
  override protected def beforeEach(): Unit = reset(service, securityDirectives)
  // #add-mock-dep

  "SampleRoute" must {
    // #add-route-test
    "call raDecToString on raDecValues post route" in {
      val response = RaDecResponse("id1", "some-value1", "some-value2")
      val request  = RaDecRequest(2.13, 2.18)
      when(service.raDecToString(request)).thenReturn(Future.successful(response))

      Post("/raDecValues", request) ~> route ~> check {
        verify(service).raDecToString(request)
        responseAs[RaDecResponse] should ===(response)
      }
    }
    // #add-route-test

    // #add-secured-route-test
    "call raDecToString on securedRaDecValues post route with some access token" in {
      val response = RaDecResponse("id1", "some-value1", "some-value2")
      val request  = RaDecRequest(2.13, 2.18)
      val policy   = RealmRolePolicy("Esw-user")
      when(securityDirectives.sPost(policy)).thenReturn(accessTokenDirective)
      when(service.raDecToString(request)).thenReturn(Future.successful(response))

      Post("/securedRaDecValues", request) ~> route ~> check {
        verify(service).raDecToString(request)
        verify(securityDirectives).sPost(policy)
        responseAs[RaDecResponse] should ===(response)
      }
    }
    // #add-secured-route-test
  }

  val connection: Connection.HttpConnection = HttpConnection(ComponentId(Prefix(TestHelper.randomSubsystem, "sample"), Service))
}
