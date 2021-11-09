package utils

import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder

trait SpecCommonHelper extends PlaySpec with GuiceOneServerPerSuite with BeforeAndAfterAll with BeforeAndAfterEach {

  lazy val wireMock = new WireMock

  val extraConfig: Map[String, Any] = {
    Map[String, Any](
      "metrics.enabled" -> false,
      "auditing.consumer.baseUri.host" -> s"${WireMockConstants.stubHost}",
      "auditing.consumer.baseUri.port" -> s"${WireMockConstants.stubPort}",
      "microservice.services.ncts.base-url" -> "http://localhost:11111"
    )
  }

  override lazy val app = new GuiceApplicationBuilder()
    .configure(extraConfig)
    .build()

  override protected def beforeAll(): Unit = {
    wireMock.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    wireMock.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    wireMock.stop()
    super.afterAll()
  }
}
