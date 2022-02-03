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
import models.responses.ErrorResponse.StatusResponseError
import models.responses.{HealthDetails, StatusResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.NctsService

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServiceStatusCheckControllerSpec extends SpecBase {

  val nctsService: NctsService = mock[NctsService]

  val mocks = Seq(
    bind[NctsService].to(nctsService)
  )

  private val healthDetailsHealthy =
    HealthDetails(healthy = true, statusChangedAt = LocalDateTime.now)
  private val healthDetailsUnhealthy =
    HealthDetails(healthy = false, statusChangedAt = LocalDateTime.now)

  "Service Status Check Controller" - {

    "must return OK and the correct view for a GET with GB/XI departures true and GB/XI arrivals false" in {
      when(nctsService.checkStatus()(any())) thenReturn
        Future(Right(
          StatusResponse(
            gbDeparturesStatus = healthDetailsHealthy,
            xiDeparturesStatus = healthDetailsHealthy,
            gbArrivalsStatus = healthDetailsUnhealthy,
            xiArrivalsStatus = healthDetailsUnhealthy,
            xmlChannelStatus = healthDetailsUnhealthy,
            webChannelStatus = healthDetailsUnhealthy,
            createdTs = LocalDateTime.now()
          )
        ))

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceStatusCheckController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe OK
      }
    }

    "must return OK and the correct view for a GET with GB/XI departures false and GB/XI arrivals true" in {
      when(nctsService.checkStatus()(any())) thenReturn
        Future(Right(StatusResponse(
          gbDeparturesStatus = healthDetailsUnhealthy,
          xiDeparturesStatus = healthDetailsUnhealthy,
          gbArrivalsStatus = healthDetailsHealthy,
          xiArrivalsStatus = healthDetailsHealthy,
          xmlChannelStatus = healthDetailsHealthy,
          webChannelStatus = healthDetailsHealthy,
          createdTs = LocalDateTime.now()
        )))

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceStatusCheckController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe OK
      }
    }


    "must return INTERNAL_SERVER_ERROR when backend returns an error response" in {
      when(nctsService.checkStatus()(any())) thenReturn Future(Left(StatusResponseError("something went wrong")))

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceStatusCheckController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
