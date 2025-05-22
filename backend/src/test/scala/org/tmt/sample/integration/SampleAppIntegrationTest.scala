package org.tmt.sample.integration

import org.apache.pekko.actor.typed.{ActorSystem, SpawnProtocol}
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.Uri.Path
import org.apache.pekko.http.scaladsl.model.*
import org.apache.pekko.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import org.apache.pekko.http.scaladsl.unmarshalling.Unmarshal
import csw.aas.core.commons.AASConnection
import csw.location.api.models.Connection.HttpConnection
import csw.location.api.models.*
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

import scala.compiletime.uninitialized
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class SampleAppIntegrationTest extends ScalaTestFrameworkTestKit with AnyWordSpecLike with Matchers with HttpCodecs {

  implicit val actorSystem: ActorSystem[SpawnProtocol.Command] = frameworkTestKit.actorSystem
  implicit val ec: ExecutionContext                            = actorSystem.executionContext
  override implicit val patienceConfig: PatienceConfig         = PatienceConfig(10.seconds)

  val locationService: LocationService = frameworkTestKit.locationService
  val hostname: String                 = Networks().hostname
  val keycloakPort                     = 8081
  val sampleAppPort                    = 8085
  val sampleWiring                     = new SampleWiring(Some(sampleAppPort))
  val appConnection: HttpConnection    = sampleWiring.settings.httpConnection

  var appLocation: HttpLocation  = uninitialized
  var appUri: Uri                = uninitialized
  var keycloakHandle: StopHandle = uninitialized

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

    "start the sample app and register with location service" in {
      val resolvedLocation = locationService.resolve(appConnection, 5.seconds).futureValue
      resolvedLocation.get.connection should ===(appConnection)
    }

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
      reDecResponse.formattedRa should ===("02:07:48.000")
      reDecResponse.formattedDec should ===("02:10:48.000")
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
      reDecResponse.formattedRa should ===("02:07:48.000")
      reDecResponse.formattedDec should ===("02:10:48.000")
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
          // Note: As of Keycloak-24.x, firstName, lastName and email are required by default (and need to be unique!)
          users = Set(
            ApplicationUser("admin", "password1", "admin", "admin", "admin@tmt.org", realmRoles = Set(eswUserRole, eswAdminRole)),
            ApplicationUser("nonAdmin", "password2", "nonAdmin", "nonAdmin", "nonAdmin@tmt.org")
          ),
          clients = Set(locationServerClient),
          realmRoles = Set(eswUserRole, eswAdminRole)
        )
      )
    )
    val embeddedKeycloak = new EmbeddedKeycloak(keycloakData, Settings(port = port, printProcessLogs = false))
    val stopHandle       = Await.result(embeddedKeycloak.startServer(), 1.minute)
    locationService.register(HttpRegistration(AASConnection.value, keycloakPort, "")).futureValue
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
