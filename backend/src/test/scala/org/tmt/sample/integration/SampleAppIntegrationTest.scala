package org.tmt.sample.integration

import akka.actor.typed.{ActorSystem, SpawnProtocol}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.unmarshalling.Unmarshal
import csw.aas.core.commons.AASConnection
import csw.location.api.models.Connection.HttpConnection
import csw.location.api.models._
import csw.location.api.scaladsl.LocationService
import csw.network.utils.Networks
import csw.testkit.scaladsl.ScalaTestFrameworkTestKit
import io.bullet.borer.Json
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.tmt.embedded_keycloak.KeycloakData.{ApplicationUser, Client, Realm}
import org.tmt.embedded_keycloak.impl.StopHandle
import org.tmt.embedded_keycloak.utils.BearerToken
import org.tmt.embedded_keycloak.{EmbeddedKeycloak, KeycloakData, Settings}
import org.tmt.sample.core.models.{RaDecRequest, RaDecResponse}
import org.tmt.sample.http.HttpCodecs
import org.tmt.sample.impl.SampleWiring

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class SampleAppIntegrationTest extends ScalaTestFrameworkTestKit with AnyWordSpecLike with Matchers with HttpCodecs {

  implicit val actorSystem: ActorSystem[SpawnProtocol.Command] = frameworkTestKit.actorSystem
  implicit val ec: ExecutionContext                            = actorSystem.executionContext
  override implicit val patienceConfig: PatienceConfig         = PatienceConfig(10.seconds)

  val locationService: LocationService = frameworkTestKit.frameworkWiring.locationService
  val hostname: String                 = Networks().hostname
  val keycloakPort                     = 8081
  val sampleAppPort                    = 8085
  val sampleWiring                     = new SampleWiring(Some(sampleAppPort))
  val appConnection: HttpConnection    = sampleWiring.settings.httpConnection

  var appLocation: HttpLocation  = _
  var appUri: Uri                = _
  var keycloakHandle: StopHandle = _

  protected override def beforeAll(): Unit = {
    super.beforeAll()
    keycloakHandle = startAndRegisterKeycloak(keycloakPort)
    sampleWiring.start(Metadata.empty).futureValue
    appLocation = locationService.resolve(appConnection, 5.seconds).futureValue.get
    appUri = Uri(appLocation.uri.toString)
  }

  protected override def afterAll(): Unit = {
    keycloakHandle.stop()
    locationService.unregister(AASConnection.value)
    sampleWiring.stop().futureValue
    super.afterAll()
  }

  "SampleWiring" must {

    // #add-route-test
    "call raDecValues and receive Response" in {
      val raDecRequest = RaDecRequest(2.13, 2.18)
      val request = HttpRequest(
        HttpMethods.POST,
        uri = appUri.withPath(Path / "raDecValues"),
        entity = HttpEntity(ContentTypes.`application/json`, Json.encode(raDecRequest).toUtf8String.getBytes)
      )

      val response: HttpResponse = Http().singleRequest(request).futureValue
      response.status should ===(StatusCode.int2StatusCode(200))
      val reDecResponse = Unmarshal(response).to[RaDecResponse].futureValue
      reDecResponse.formattedRa should ===("8h 8m 9.602487087684134s")
      reDecResponse.formattedDec should ===(s"124°54'17.277618670114634\"")
    }
    // #add-route-test

    // #add-secured-route-test
    "call securedRaDecValues and receive valid response when user have required role" in {
      val token     = getToken("admin", "password1")()
      val raDecRequest = RaDecRequest(2.13, 2.18)
      val request = HttpRequest(
        HttpMethods.POST,
        uri = appUri.withPath(Path / "securedRaDecValues"),
        headers = token.map(x => Seq(Authorization(OAuth2BearerToken(x)))).getOrElse(Nil),
        entity = HttpEntity(ContentTypes.`application/json`, Json.encode(raDecRequest).toUtf8String.getBytes)
      )

      val response: HttpResponse = Http().singleRequest(request).futureValue
      response.status should ===(StatusCode.int2StatusCode(200))
      val reDecResponse = Unmarshal(response).to[RaDecResponse].futureValue
      reDecResponse.formattedRa should ===("8h 8m 9.602487087684134s")
      reDecResponse.formattedDec should ===(s"124°54'17.277618670114634\"")
    }
    // #add-secured-route-test

    // #add-secured-route-failure-test
    "call securedRaDecValues and receive 403 when user does not have required role" in {
      val token     = getToken("nonAdmin", "password2")()
      val raDecRequest = RaDecRequest(2.13, 2.18)
      val request = HttpRequest(
        HttpMethods.POST,
        uri = appUri.withPath(Path / "securedRaDecValues"),
        headers = token.map(x => Seq(Authorization(OAuth2BearerToken(x)))).getOrElse(Nil),
        entity = HttpEntity(ContentTypes.`application/json`, Json.encode(raDecRequest).toUtf8String.getBytes)
      )

      val response: HttpResponse = Http().singleRequest(request).futureValue
      response.status should ===(StatusCode.int2StatusCode(403))
    }
    // #add-secured-route-failure-test
  }

  private def startAndRegisterKeycloak(port: Int): StopHandle = {
    val eswUserRole  = "Esw-user"
    val eswAdminRole = "Esw-admin"
    val locationServerClient =
      Client(name = "tmt-frontend-app", clientType = "public", passwordGrantEnabled = true)
    val keycloakData = KeycloakData(
      realms = Set(
        Realm(
          name = "TMT",
          users = Set(
            ApplicationUser("admin", "password1", realmRoles = Set(eswUserRole, eswAdminRole)),
            ApplicationUser("nonAdmin", "password2")
          ),
          clients = Set(locationServerClient),
          realmRoles = Set(eswUserRole, eswAdminRole)
        )
      )
    )
    val embeddedKeycloak = new EmbeddedKeycloak(keycloakData, Settings(port = port, printProcessLogs = false))
    val stopHandle       = Await.result(embeddedKeycloak.startServer(), 1.minute)
    locationService.register(HttpRegistration(AASConnection.value, keycloakPort, "auth")).futureValue
    stopHandle
  }

  private def getToken(userName: String, password: String): () => Some[String] = { () =>
    Some(
      BearerToken
        .fromServer(
          realm = "TMT",
          client = "tmt-frontend-app",
          host = hostname,
          port = keycloakPort,
          username = userName,
          password = password
        )
        .token
    )
  }

}
