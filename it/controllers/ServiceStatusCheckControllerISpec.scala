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

import models.responses.StatusResponse
import play.api.libs.json.Json
import play.api.test.Helpers._
import utils.SpecCommonHelper

class ServiceStatusCheckControllerISpec extends SpecCommonHelper {

  "check status" should {

    "return OK with the correct view for a successful response when service is healthy" in {
      stubGet("/ncts/status-check", OK, Json.toJson(StatusResponse(gbDeparturesHealthy = true)).toString)

      val response = ws.url(s"${baseUrl}/service-availability").get()

      whenReady(response) { result =>
        result.status mustBe OK
        result.body must include(messages("service.availability.heading"))
        result.body must include(messages("service.availability.web.channel.card.available"))
        result.body must include(messages("service.availability.web.channel.card.available.description"))
      }
    }

    "return OK with the correct view for a successful response when service is not healthy" in {
      stubGet("/ncts/status-check", OK, Json.toJson(StatusResponse(gbDeparturesHealthy = false)).toString)

      val response = ws.url(s"${baseUrl}/service-availability").get()

      whenReady(response) { result =>
        result.status mustBe OK
        result.body must include(messages("service.availability.heading"))
        result.body must include(messages("service.availability.web.channel.card.unavailable.description"))
        result.body must include(messages("service.availability.web.channel.card.unavailable"))
      }
    }

    "return INTERNAL_SERVER_ERROR when fail to parse the json response" in {
      stubGet("/ncts/status-check", OK, "")

      val response = ws.url(s"${baseUrl}/service-availability").get()

      whenReady(response) { result =>
        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

    "return INTERNAL_SERVER_ERROR when there is an error" in {
      stubGet("/ncts/status-check", SERVICE_UNAVAILABLE, Json.toJson(StatusResponse(gbDeparturesHealthy = false)).toString)

      val response = ws.url(s"${baseUrl}/service-availability").get()

      whenReady(response) { result =>
        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
