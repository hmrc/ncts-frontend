/*
 * Copyright 2021 HM Revenue & Customs
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
import models.responses.ErrorResponse.StatusResponseError
import models.responses.{ErrorResponse, StatusResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import uk.gov.hmrc.http.HttpClient

import scala.concurrent.Future

class NctsConnectorSpec extends SpecBase {

  private implicit val appConfig = app.injector.instanceOf[FrontendAppConfig]

  private val mockHttp = mock[HttpClient]

  private val nctsConnector = new NctsConnector(mockHttp, appConfig)

  "Ncts Connector" - {
    "should return a valid response" in {
      when(mockHttp.GET[Either[ErrorResponse, StatusResponse]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Right(StatusResponse(departuresApiHealthy = true))))

      val result = nctsConnector.checkStatus().futureValue

      result mustBe Right(StatusResponse(departuresApiHealthy = true))
    }

    "should return a scheme response error when an error occurs" in {
      when(mockHttp.GET[Either[ErrorResponse, StatusResponse]](any(), any(), any())(any(), any(), any()))
        .thenReturn(Future.successful(Left(StatusResponseError("something went wrong"))))

      val result = nctsConnector.checkStatus().futureValue

      result mustBe Left(StatusResponseError("something went wrong"))
    }
  }
}
