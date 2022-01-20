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

import com.typesafe.config.ConfigFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import play.api.Configuration
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import utils.SpecCommonHelper

import java.io.File
import scala.concurrent.Await

class PlannedDowntimeControllerISpec extends SpecCommonHelper {

  override implicit lazy val app = {

    val downtimeConfig = ConfigFactory.parseFile(new File("it/config/planned-downtime.conf"))

    new GuiceApplicationBuilder()
      .configure(Configuration(downtimeConfig))
      .build
  }

  "planned-downtime" when {

    val response = ws.url(s"${baseUrl}/planned-downtime").get()

    val result = Await.result(response, 2.seconds)

    val document: Document = Jsoup.parse(result.body)

    "there is downtime config" should {
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
    }

    "there is downtime planned for arrivals" should {

      "have the arrivals subheading" in {
        document.select("h2").first().text() mustBe messages("service.planned-downtime.ncts.arrivals")
      }

      "have a table for arrivals with the correct heading" in {
        document.select("table:nth-child(2) > thead > tr > th:nth-child(1)").get(0).text() mustBe messages("service.planned-downtime.system.name")
        document.select("table:nth-child(2) > thead > tr > th:nth-child(2)").get(0).text() mustBe messages("service.planned-downtime.start")
        document.select("table:nth-child(2) > thead > tr > th:nth-child(3)").get(0).text() mustBe messages("service.planned-downtime.end")
      }

      "have a table for arrivals with a row for GB" in {
        document.select("table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.planned-downtime.ncts.gb.arrivals")

        document.select("table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2)")
          .get(0).text() mustBe "Date: Monday 15 March 2021 Time: 8:15AM"

        document.select("table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(3)")
          .get(0).text() mustBe "Date: Tuesday 16 March 2021 Time: 5PM"
      }

      "have a table for arrivals with a row for XI" in {
        document.select("table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.planned-downtime.ncts.xi.arrivals")

        document.select("table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(2)")
          .get(0).text() mustBe "Date: Monday 15 March 2021 Time: 8AM"

        document.select("table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(3)")
          .get(0).text() mustBe "Date: Tuesday 16 March 2021 Time: 5PM"
      }

      "have some content about submissions when the service is down and an apology for arrivals" in {
        document.getElementsByClass("govuk-body").first().text() mustBe messages("service.planned-downtime.p1")
        document.getElementsByClass("govuk-body").get(1).text() mustBe messages("service.planned-downtime.p2")
      }
    }

    "there is downtime planned for departures" should {

      "have the departures subheading" in {
        document.select("h2").get(1).text() mustBe messages("service.planned-downtime.ncts.departures")
      }

      "have a table for departures with the correct heading" in {
        document.select("table:nth-child(6) > thead > tr > th:nth-child(1)").get(0).text() mustBe messages("service.planned-downtime.system.name")
        document.select("table:nth-child(6) > thead > tr > th:nth-child(2)").get(0).text() mustBe messages("service.planned-downtime.start")
        document.select("table:nth-child(6) > thead > tr > th:nth-child(3)").get(0).text() mustBe messages("service.planned-downtime.end")
      }

      "have a table for departures with a row for GB" in {
        document.select("table:nth-child(6) > tbody > tr:nth-child(1) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.planned-downtime.ncts.gb.departures")

        document.select("table:nth-child(6) > tbody > tr:nth-child(1) > td:nth-child(2)")
          .get(0).text() mustBe "Date: Monday 15 March 2021 Time: 8AM"

        document.select("table:nth-child(6) > tbody > tr:nth-child(1) > td:nth-child(3)")
          .get(0).text() mustBe "Date: Tuesday 16 March 2021 Time: 5:45PM"
      }

      "have a table for departures with a row for XI" in {
        document.select("table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.planned-downtime.ncts.xi.departures")

        document.select("table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(2)")
          .get(0).text() mustBe "Date: Monday 15 March 2021 Time: 8AM"

        document.select("table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(3)")
          .get(0).text() mustBe "Date: Tuesday 16 March 2021 Time: 5PM"
      }

      "have some content about submissions when the service is down and an apology for departures" in {
        document.getElementsByClass("govuk-body").get(2).text() mustBe messages("service.planned-downtime.p1")
        document.getElementsByClass("govuk-body").last().text() mustBe messages("service.planned-downtime.p2")
      }
    }
  }

}
