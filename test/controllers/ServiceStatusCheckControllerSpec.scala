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

package controllers

import base.SpecBase
import models.responses.ErrorResponse.StatusResponseError
import models.responses.StatusResponse
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.NctsService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServiceStatusCheckControllerSpec extends SpecBase {

  val nctsService = mock[NctsService]

  val mocks = Seq(
    bind[NctsService].to(nctsService)
  )

  "Service Status Check Controller" - {

    "must return OK and the correct view for a GET for GB" in {
      when(nctsService.checkStatus()(any())) thenReturn Future(Right(StatusResponse(true,false)))

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceStatusCheckController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustBe OK
      }
    }

    "must return OK and the correct view for a GET for XI" in {
      when(nctsService.checkStatus()(any())) thenReturn Future(Right(StatusResponse(false,true)))

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceStatusCheckController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustBe OK
      }
    }


    "must return INTERNAL_SERVER_ERROR when backend returns an error response" in {
      when(nctsService.checkStatus()(any())) thenReturn Future(Left(StatusResponseError("something went wrong")))

      val application = applicationBuilder().overrides(mocks).build()

      running(application) {

        val request = FakeRequest(GET, routes.ServiceStatusCheckController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
