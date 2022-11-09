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

import models.responses.{Downtime, DowntimeResponse}
import models.{XML, _}
import org.jsoup.Jsoup
import play.api.libs.json.Json
import play.api.test.Helpers._
import utils.SpecCommonHelper

import java.time.LocalDateTime
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DowntimeHistoryControllerISpec extends SpecCommonHelper {

  "check status" should {

    "return OK with the correct view for a successful response when service is healthy" in {
      stubGet(
        "/ncts/downtime-history",
        OK,
        Json
          .toJson(
            DowntimeResponse(
              Seq(
                Downtime(GBDepartures, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(XIDepartures, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(GBArrivals, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(XIArrivals, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(Web, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(XML, LocalDateTime.now().minusHours(1), LocalDateTime.now())
              ),
              LocalDateTime.now()
            )
          )
          .toString
      )

      val response = Await.result(ws.url(s"$baseUrl/downtime-history").get(), Duration.Inf)

      val document = Jsoup.parse(response.body)

      response.status.mustBe(OK)
      document.getElementsByTag("h1").first().text() mustBe messages("service.downtime.history.heading")
    }

    "return INTERNAL_SERVER_ERROR when failing to parse the json response" in {
      stubGet("/ncts/downtime-history", OK, "")

      val response = Await.result(ws.url(s"$baseUrl/downtime-history").get(), Duration.Inf)

      val document = Jsoup.parse(response.body)

      response.status.mustBe(INTERNAL_SERVER_ERROR)
      document.getElementsByTag("h1").first().text() mustBe messages("error.title")
    }
    "return INTERNAL_SERVER_ERROR when there is an error" in {
      stubGet(
        "/ncts/downtime-history",
        SERVICE_UNAVAILABLE,
        Json
          .toJson(
            DowntimeResponse(
              Seq(
                Downtime(GBDepartures, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(XIDepartures, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(GBArrivals, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(XIArrivals, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(Web, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
                Downtime(XML, LocalDateTime.now().minusHours(1), LocalDateTime.now())
              ),
              LocalDateTime.now()
            )
          )
          .toString
      )

      val response = Await.result(ws.url(s"$baseUrl/downtime-history").get(), Duration.Inf)

      val document = Jsoup.parse(response.body)

      response.status.mustBe(INTERNAL_SERVER_ERROR)
      document.getElementsByTag("h1").first().text() mustBe messages("error.title")
    }
  }
}
