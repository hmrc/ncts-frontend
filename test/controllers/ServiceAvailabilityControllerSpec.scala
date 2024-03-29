/*
 * Copyright 2023 HM Revenue & Customs
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
import models.responses.StatusResponse
import org.mockito.ArgumentMatchers.any
import play.api.inject.{Binding, bind}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.HealthCheckService
import utils.HealthDetailsExamples._
import org.mockito.Mockito._

import java.time.LocalDateTime
import scala.concurrent.Future

class ServiceAvailabilityControllerSpec extends SpecBase {

  val healthCheckService: HealthCheckService = mock[HealthCheckService]

  val mocks: Seq[Binding[HealthCheckService]] = Seq(
    bind[HealthCheckService].to(healthCheckService)
  )

  "Service Status Check Controller" - {

    "must return OK and the correct view for a GET with GB/XI departures true and GB/XI arrivals false" in {
      when(healthCheckService.checkStatus()(any())) thenReturn
        Future(
          Some(
            StatusResponse(
              gbDeparturesStatus = healthDetailsHealthy,
              xiDeparturesStatus = healthDetailsHealthy,
              gbArrivalsStatus = healthDetailsUnhealthy,
              xiArrivalsStatus = healthDetailsUnhealthy,
              xmlChannelStatus = healthDetailsUnhealthy,
              webChannelStatus = healthDetailsUnhealthy,
              ppnStatus = healthDetailsUnhealthy,
              createdTs = LocalDateTime.now()
            )
          )
        )(ec)

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceAvailabilityController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe OK
      }
    }

    "must return OK and the correct view for a GET with GB/XI departures false and GB/XI arrivals true" in {
      when(healthCheckService.checkStatus()(any())) thenReturn
        Future(
          Some(
            StatusResponse(
              gbDeparturesStatus = healthDetailsUnhealthy,
              xiDeparturesStatus = healthDetailsUnhealthy,
              gbArrivalsStatus = healthDetailsHealthy,
              xiArrivalsStatus = healthDetailsHealthy,
              xmlChannelStatus = healthDetailsHealthy,
              webChannelStatus = healthDetailsHealthy,
              ppnStatus = healthDetailsHealthy,
              createdTs = LocalDateTime.now()
            )
          )
        )(ec)

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceAvailabilityController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe OK
      }
    }

    "must return INTERNAL_SERVER_ERROR when backend returns an error response" in {

      when(healthCheckService.checkStatus()(any())) thenReturn Future(None)(ec)

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceAvailabilityController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
