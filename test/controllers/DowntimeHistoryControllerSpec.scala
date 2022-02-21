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

package controllers

import base.SpecBase
import models.Channel
import models.responses.ErrorResponse.DowntimeResponseError
import models.responses.{Downtime, DowntimeResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DowntimeHistoryService

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DowntimeHistoryControllerSpec extends SpecBase {

  val downtimeHistoryService: DowntimeHistoryService = mock[DowntimeHistoryService]

  val mocks = Seq(
    bind[DowntimeHistoryService].to(downtimeHistoryService)
  )
  "DowntimeHistoryController" - {

    "must return OK and the correct view for a GET with downtime history" in {

      when(downtimeHistoryService.checkOutageHistory()(any())) thenReturn
        Future(Right(
          DowntimeResponse(
            Seq(
              Downtime(Channel.gbDepartures, LocalDateTime.now(), LocalDateTime.now())
            ))))

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.DowntimeHistoryController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe OK
      }
    }

    "must return INTERNAL_SERVER_ERROR when backend returns an error response" in {
      when(downtimeHistoryService.checkOutageHistory()(any())) thenReturn Future(Left(DowntimeResponseError("something went wrong")))

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.DowntimeHistoryController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
