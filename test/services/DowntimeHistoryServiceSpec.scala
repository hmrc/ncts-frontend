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

package services

import base.SpecBase
import connectors.NCTSConnector
import models.{Channel, GBDepartures}
import models.responses.ErrorResponse.DowntimeResponseError
import models.responses.{Downtime, DowntimeResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DowntimeHistoryServiceSpec extends SpecBase {

  val nctsConnector = mock[NCTSConnector]
  val service = new DowntimeHistoryService(nctsConnector)


  //LocalDateTime.of(2022, 1, 1, 10, 25, 55)

  "checkOutageHistory" - {
    "return a valid status response" in {
      when(nctsConnector.checkOutageHistory()(any())) thenReturn
        Future(Right(
          DowntimeResponse(
            Seq(
              Downtime(
                GBDepartures,
                LocalDateTime.of(2022, 1, 1, 10, 25, 55),
                LocalDateTime.of(2022, 1, 1, 10, 25, 55)
              )), LocalDateTime.of(2022, 1, 1, 10, 25, 55))))

      val result = service.checkOutageHistory().futureValue


      result.fold(
        _ => "should not return an error response",
        response => response mustBe DowntimeResponse(
          Seq(
            Downtime(
              GBDepartures,
              LocalDateTime.of(2022, 1, 1, 10, 25, 55),
              LocalDateTime.of(2022, 1, 1, 10, 25, 55)
            )), LocalDateTime.of(2022, 1, 1, 10, 25, 55)
        )
      )
    }

    "return an error response when error occurs" in {
      when(nctsConnector.checkStatus()(any())) thenReturn Future.successful(Left(DowntimeResponseError("something went wrong")))
      val result = service.checkOutageHistory().futureValue

      result.fold(
        errorResponse => errorResponse mustBe DowntimeResponseError("something went wrong"),
        _ => "should not succeed"
      )
    }
  }
}
