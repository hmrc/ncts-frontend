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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import utils.SpecCommonHelper

import scala.concurrent.Await

class PlannedDowntimeControllerNoDowntimeISpec extends SpecCommonHelper {

  override implicit lazy val app = {

    new GuiceApplicationBuilder()
      .configure(Map("planned-downtime" -> Seq()))
      .build
  }

  "planned-downtime" when {

    val response = ws.url(s"${baseUrl}/planned-downtime").get()

    val result = Await.result(response, 2.seconds)

    val document: Document = Jsoup.parse(result.body)

    "there is no downtime config" should {

      "return OK" in {
        result.status mustBe OK
      }

      "have a back link to the home page" in {
        document.getElementsByClass("govuk-back-link").first()
          .attr("href") mustBe controllers.routes.IndexController.onPageLoad.url
      }

      "have the correct heading" in {
        document.select("h1").first().text() mustBe messages("service.planned-downtime.heading")
      }

      "not have any tables of downtime information" in {
        document.getElementsByTag("table").size() mustBe 0
      }

      "have the arrivals subheading" in {
        document.select("h2").first().text() mustBe messages("service.planned-downtime.ncts.arrivals")
      }

      "have text about there being no downtime for arrivals" in {
        document.getElementsByClass("govuk-body").first().text() mustBe messages("service.planned-downtime.no.downtime.planned.arrivals")
      }

      "have the departures subheading" in {
        document.select("h2").get(1).text() mustBe messages("service.planned-downtime.ncts.departures")
      }

      "have text about there being no downtime for departures" in {
        document.getElementsByClass("govuk-body").last().text() mustBe messages("service.planned-downtime.no.downtime.planned.departures")
      }
    }
  }
}
