package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import play.api.http.Status
import uk.gov.hmrc.http.HeaderCarrier
import utils.SpecCommonHelper

class NctsConnectorISpec extends PlaySpec with SpecCommonHelper with ScalaFutures{

  val connector = app.injector.instanceOf[NctsConnector]
  implicit val hc=HeaderCarrier()
  "check status" should {
    "return the service status of the Web channel" in{

      wireMock.wireMockServer.stubFor(
        get(urlMatching("/service-status"))
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody("")
          )
      )
      val serviceStatus=connector.checkStatus()

    }

  }

}
