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
import org.jsoup.Jsoup
import play.api.libs.json.Json
import play.api.test.Helpers._
import utils.SpecCommonHelper

import java.time.LocalDateTime
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ServiceStatusCheckControllerISpec extends SpecCommonHelper {

  "check status" should {

    "return OK with the correct view for a successful response when service is healthy" in {
      stubGet("/ncts/status-check", OK,
        Json.toJson(StatusResponse(
          gbDeparturesHealthy = true, xiDeparturesHealthy = true, gbArrivalsHealthy = true,
          xiArrivalsHealthy = true, apiChannelHealthy = true, webChannelHealthy = true, createdTs = LocalDateTime.now())).toString)

      val response = Await.result(ws.url(s"$baseUrl/service-availability").get(), Duration.Inf)

      val document = Jsoup.parse(response.body)

      response.status.mustBe(OK)
      document.getElementsByTag("h1").first().text() mustBe messages("service.availability.heading")
    }

    "return OK with the correct view for a successful response when service is not healthy" in {
      stubGet("/ncts/status-check", OK,
        Json.toJson(StatusResponse(gbDeparturesHealthy = false, xiDeparturesHealthy = false, gbArrivalsHealthy = false,
          xiArrivalsHealthy = false, apiChannelHealthy = false, webChannelHealthy = false, createdTs = LocalDateTime.now())).toString)

      val response = Await.result(ws.url(s"$baseUrl/service-availability").get(), Duration.Inf)

      val document = Jsoup.parse(response.body)

      response.status.mustBe(OK)
      document.getElementsByTag("h1").first().text() mustBe messages("service.availability.heading")

    }

    "return INTERNAL_SERVER_ERROR when failing to parse the json response" in {
      stubGet("/ncts/status-check", OK, "")

      val response = Await.result(ws.url(s"$baseUrl/service-availability").get(), Duration.Inf)

      val document = Jsoup.parse(response.body)

      response.status.mustBe(INTERNAL_SERVER_ERROR)
      document.getElementsByTag("h1").first().text() mustBe messages("error.title")
    }

    "return INTERNAL_SERVER_ERROR when there is an error" in {
      stubGet("/ncts/status-check", SERVICE_UNAVAILABLE,
        Json.toJson(StatusResponse(gbDeparturesHealthy = false, xiDeparturesHealthy = false, gbArrivalsHealthy = true,
          xiArrivalsHealthy = true, apiChannelHealthy = true, webChannelHealthy = true, createdTs = LocalDateTime.now())).toString)

      val response = Await.result(ws.url(s"$baseUrl/service-availability").get(), Duration.Inf)

      val document = Jsoup.parse(response.body)

      response.status.mustBe(INTERNAL_SERVER_ERROR)
      document.getElementsByTag("h1").first().text() mustBe messages("error.title")
    }
  }
}
