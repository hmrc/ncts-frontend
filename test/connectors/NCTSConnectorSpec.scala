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

import base.SpecBase
import config.FrontendAppConfig
import models.Channel
import models.responses.ErrorResponse.{DowntimeResponseError, StatusResponseError}
import models.responses.{Downtime, DowntimeResponse, ErrorResponse, StatusResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import uk.gov.hmrc.http.HttpClient
import utils.HealthDetailsExamples._

import java.time.LocalDateTime
import scala.concurrent.Future


class NCTSConnectorSpec extends SpecBase {

  private implicit val appConfig = app.injector.instanceOf[FrontendAppConfig]

  private val mockHttp = mock[HttpClient]

  private val nctsConnector = new NCTSConnector(mockHttp, appConfig)

  "NCTS Connector" - {
    "checkStatus" - {
      "should return a valid response with GB/XI departures true and GB/XI arrivals false" in {
        val response = StatusResponse(
          gbDeparturesStatus = healthDetailsHealthy,
          xiDeparturesStatus = healthDetailsHealthy,
          gbArrivalsStatus = healthDetailsUnhealthy,
          xiArrivalsStatus = healthDetailsUnhealthy,
          xmlChannelStatus = healthDetailsUnhealthy,
          webChannelStatus = healthDetailsUnhealthy,
          createdTs = LocalDateTime.now()
        )

        when(mockHttp.GET[Either[ErrorResponse, StatusResponse]](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Right(response)))

        val result = nctsConnector.checkStatus().futureValue

        result mustBe Right(response)
      }

      "should return a valid response with GB/XI departures false and GB/XI arrivals true" in {
        val response = StatusResponse(
          gbDeparturesStatus = healthDetailsUnhealthy,
          xiDeparturesStatus = healthDetailsUnhealthy,
          gbArrivalsStatus = healthDetailsHealthy,
          xiArrivalsStatus = healthDetailsHealthy,
          xmlChannelStatus = healthDetailsHealthy,
          webChannelStatus = healthDetailsHealthy,
          createdTs = LocalDateTime.now()
        )

        when(mockHttp.GET[Either[ErrorResponse, StatusResponse]](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Right(response)))

        val result = nctsConnector.checkStatus().futureValue

        result mustBe Right(response)
      }

      "should return an error response when an error occurs" in {
        when(mockHttp.GET[Either[ErrorResponse, StatusResponse]](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Left(StatusResponseError("something went wrong"))))

        val result = nctsConnector.checkStatus().futureValue

        result mustBe Left(StatusResponseError("something went wrong"))
      }
    }

    "checkOutageHistory" - {
      "should return a valid response with GB/XI departures true and GB/XI arrivals false" in {
        val response = DowntimeResponse(
          Seq(
            Downtime(
              Channel.gbDepartures,
              LocalDateTime.of(2022, 1, 1, 10, 25, 55),
              LocalDateTime.of(2022, 1, 1, 10, 25, 55)
            )))

        when(mockHttp.GET[Either[ErrorResponse, DowntimeResponse]](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Right(response)))

        val result = nctsConnector.checkOutageHistory().futureValue

        result mustBe Right(response)
      }

      "should return a valid response with GB/XI departures false and GB/XI arrivals true" in {
        val response = DowntimeResponse(
          Seq(
            Downtime(
              Channel.gbDepartures,
              LocalDateTime.of(2022, 1, 1, 10, 25, 55),
              LocalDateTime.of(2022, 1, 1, 10, 25, 55)
            )))

        when(mockHttp.GET[Either[ErrorResponse, DowntimeResponse]](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Right(response)))

        val result = nctsConnector.checkOutageHistory().futureValue

        result mustBe Right(response)
      }

      "should return an error response when an error occurs" in {
        when(mockHttp.GET[Either[ErrorResponse, DowntimeResponse]](any(), any(), any())(any(), any(), any()))
          .thenReturn(Future.successful(Left(DowntimeResponseError("something went wrong"))))

        val result = nctsConnector.checkOutageHistory().futureValue

        result mustBe Left(DowntimeResponseError("something went wrong"))
      }
    }
  }
}
