package utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.{WireMock => WireMockClient}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

object WireMockConstants {
  val stubPort = 11111
  val stubHost = "localhost"
}

class WireMock {

  var wireMockServer: WireMockServer = new WireMockServer(wireMockConfig().port(WireMockConstants.stubPort))

  def host(): String = s"http://${WireMockConstants.stubHost}:${WireMockConstants.stubPort}"

  def start(): Unit = {
    if (!wireMockServer.isRunning) {
      wireMockServer.start()
      WireMockClient.configureFor(WireMockConstants.stubHost, WireMockConstants.stubPort)
    }
  }

  def stop(): Unit = {
    wireMockServer.stop()
  }

  def resetAll(): Unit = {
    wireMockServer.resetMappings()
    wireMockServer.resetRequests()
  }
}
