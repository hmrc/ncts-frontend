/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import models.GBDepartures
import models.responses.{Downtime, DowntimeResponse, StatusResponse}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import utils.HealthDetailsExamples._
import utils.WireMockHelper

import java.time.LocalDateTime
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class NCTSConnectorSpec
    extends AnyFreeSpec
    with Matchers
    with WsScalaTestClient
    with GuiceOneAppPerSuite
    with WireMockHelper
    with ScalaFutures {

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .configure("microservice.services.ncts.port" -> server.port())
    .build()

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private lazy val nctsConnector: NCTSConnector = app.injector.instanceOf[NCTSConnector]

  val baseUrl = "/ncts"

  "NCTS Connector" - {
    "checkStatus" - {

      val urlUnderTest = s"$baseUrl/status-check"

      "should return a valid response with GB/XI departures true and GB/XI arrivals false" in {
        val response = StatusResponse(
          gbDeparturesStatus = healthDetailsHealthy,
          xiDeparturesStatus = healthDetailsHealthy,
          gbArrivalsStatus = healthDetailsUnhealthy,
          xiArrivalsStatus = healthDetailsUnhealthy,
          xmlChannelStatus = healthDetailsUnhealthy,
          webChannelStatus = healthDetailsUnhealthy,
          ppnStatus = healthDetailsUnhealthy,
          createdTs = LocalDateTime.now()
        )

        server.stubFor(
          get(urlEqualTo(urlUnderTest))
            .willReturn(ok(Json.toJson(response).toString()))
        )

        val result = Await.result(nctsConnector.checkStatus(), Duration.Inf)

        result mustBe Some(response)
      }

      "should return a valid response with GB/XI departures false and GB/XI arrivals true" in {
        val response = StatusResponse(
          gbDeparturesStatus = healthDetailsUnhealthy,
          xiDeparturesStatus = healthDetailsUnhealthy,
          gbArrivalsStatus = healthDetailsHealthy,
          xiArrivalsStatus = healthDetailsHealthy,
          xmlChannelStatus = healthDetailsHealthy,
          webChannelStatus = healthDetailsHealthy,
          ppnStatus = healthDetailsHealthy,
          createdTs = LocalDateTime.now()
        )

        server.stubFor(
          get(urlEqualTo(urlUnderTest))
            .willReturn(ok(Json.toJson(response).toString()))
        )

        val result = nctsConnector.checkStatus().futureValue

        result mustBe Some(response)
      }

      "should return None when NCTS returns a Not Found" in {

        server.stubFor(
          get(urlEqualTo(urlUnderTest))
            .willReturn(notFound())
        )

        val result = nctsConnector.checkStatus().futureValue

        result mustBe None
      }

      "should throw an UpstreamErrorResponse when NCTS" - {

        "returns 200 but with an invalid payload" in {

          server.stubFor(
            get(urlEqualTo(urlUnderTest))
              .willReturn(ok("""{"foo": "bar", "id": 123}"""))
          )

          nctsConnector.checkStatus().failed.futureValue mustBe a[RuntimeException]
        }
      }

      "should throw an UpstreamErrorResponse when NCTS" - {

        "returns Bad request" in {

          val errorMessage = "Please try again"

          server.stubFor(
            get(urlEqualTo(urlUnderTest))
              .willReturn(badRequest().withBody(errorMessage))
          )

          nctsConnector.checkStatus().failed.futureValue mustBe an[UpstreamErrorResponse]
        }

        "returns Internal server error" in {

          val errorMessage = "Something went wrong"

          server.stubFor(
            get(urlEqualTo(urlUnderTest))
              .willReturn(serverError().withBody(errorMessage))
          )

          nctsConnector.checkStatus().failed.futureValue mustBe an[UpstreamErrorResponse]
        }

        "returns Service unavailable" in {

          val errorMessage = "Could not reach service"

          server.stubFor(
            get(urlEqualTo(urlUnderTest))
              .willReturn(serviceUnavailable().withBody(errorMessage))
          )

          nctsConnector.checkStatus().failed.futureValue mustBe an[UpstreamErrorResponse]
        }
      }
    }

    "checkOutageHistory" - {

      val urlUnderTest = s"$baseUrl/downtime-history"

      "should return a valid response with GB/XI departures true and GB/XI arrivals false" in {
        val response = DowntimeResponse(
          Seq(
            Downtime(
              GBDepartures,
              LocalDateTime.of(2022, 1, 1, 10, 25, 55),
              LocalDateTime.of(2022, 1, 1, 10, 25, 55)
            )
          ),
          LocalDateTime.of(2022, 1, 1, 10, 25, 55)
        )

        server.stubFor(
          get(urlEqualTo(urlUnderTest))
            .willReturn(ok(Json.toJson(response).toString()))
        )

        val result = nctsConnector.getDowntimeHistory().futureValue

        result mustBe Some(response)
      }

      "should return a valid response with GB/XI departures false and GB/XI arrivals true" in {
        val response = DowntimeResponse(
          Seq(
            Downtime(
              GBDepartures,
              LocalDateTime.of(2022, 1, 1, 10, 25, 55),
              LocalDateTime.of(2022, 1, 1, 10, 25, 55)
            )
          ),
          LocalDateTime.of(2022, 1, 1, 10, 25, 55)
        )

        server.stubFor(
          get(urlEqualTo(urlUnderTest))
            .willReturn(ok(Json.toJson(response).toString()))
        )

        val result = nctsConnector.getDowntimeHistory().futureValue

        result mustBe Some(response)
      }

      "should throw an UpstreamErrorResponse when NCTS" - {

        "returns 200 but with an invalid payload" in {

          server.stubFor(
            get(urlEqualTo(urlUnderTest))
              .willReturn(ok("""{"foo": "bar", "id": 123}"""))
          )

          nctsConnector.getDowntimeHistory().failed.futureValue mustBe a[RuntimeException]
        }
      }

      "should return None when NCTS returns a Not Found" in {

        server.stubFor(
          get(urlEqualTo(urlUnderTest))
            .willReturn(notFound())
        )

        val result = nctsConnector.getDowntimeHistory().futureValue

        result mustBe None
      }

      "should throw an UpstreamErrorResponse when NCTS" - {

        "returns Bad request" in {

          val errorMessage = "Please try again"

          server.stubFor(
            get(urlEqualTo(urlUnderTest))
              .willReturn(badRequest().withBody(errorMessage))
          )

          nctsConnector.getDowntimeHistory().failed.futureValue mustBe an[UpstreamErrorResponse]
        }

        "returns Internal server error" in {

          val errorMessage = "Something went wrong"

          server.stubFor(
            get(urlEqualTo(urlUnderTest))
              .willReturn(badRequest().withBody(errorMessage))
          )

          nctsConnector.getDowntimeHistory().failed.futureValue mustBe an[UpstreamErrorResponse]
        }

        "returns Service unavailable" in {

          val errorMessage = "Could not reach service"

          server.stubFor(
            get(urlEqualTo(urlUnderTest))
              .willReturn(badRequest().withBody(errorMessage))
          )

          nctsConnector.getDowntimeHistory().failed.futureValue mustBe an[UpstreamErrorResponse]
        }
      }
    }
  }
}
