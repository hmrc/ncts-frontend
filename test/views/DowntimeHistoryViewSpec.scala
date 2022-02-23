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

package views

import base.SpecBase
import models._
import models.responses.Downtime
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Injecting
import views.html.DowntimeHistoryView

import java.time.LocalDateTime

class DowntimeHistoryViewSpec extends SpecBase with Injecting {

  "DowntimeHistoryView" - {

    def view: DowntimeHistoryView = inject[DowntimeHistoryView]

    lazy val document: Document = Jsoup.parse(view(downtimeHistory).body)
    lazy val documentNoDowntime: Document = Jsoup.parse(view(Seq.empty).body)

    /*
        "should have the correct breadcrumbs" in {
          breadcrumb(0, document).text() mustBe "Home"
          breadcrumb(0, document).attr("href") mustBe govukHomeLink
          breadcrumb(1, document).text() mustBe "Service availability and Planned downtime"
          breadcrumb(1, document).attr("href") mustBe homeLink
          breadcrumb(2, document).text() mustBe "Planned downtime"
          breadcrumb(2, document).attr("href") mustBe plannedDowntimeLink
        }
    */

    /*    "should have the correct heading" in {
          document.select("h1").first().text() mustBe messages("service.planned-downtime.heading")
        }*/

    /*    "should have a get help link" in {
          document.body().select(".hmrc-report-technical-issue").first()
            .attr("href") mustBe getHelpUrl
        }*/

    "when there are downtime history" - {

      "should have the subheading" in {
        document.select("h2").first().text() mustBe messages("service.downtime.history.h2")
      }

      "should have a table for downtime history with the correct heading" in {
        document.select("div:nth-child(2) > div > table > thead > tr > th:nth-child(1)").get(0).text() mustBe messages("service.downtime.history.component.name")
        document.select("div:nth-child(2) > div > table > thead > tr > th:nth-child(2)").get(0).text() mustBe messages("service.downtime.history.start")
        document.select("div:nth-child(2) > div > table > thead > tr > th:nth-child(3)").get(0).text() mustBe messages("service.downtime.history.end")
      }

      "should have rows for GB Departures and Arrivals" in {
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(1) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.downtime.history.ncts.gb.departures")
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(1) > td:nth-child(2)")
          .get(0).text() mustBe "Date: 1 January 2022 Time: 10:25am"
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(1) > td:nth-child(3)")
          .get(0).text() mustBe "Date: 2 January 2022 Time: 10:25am"


        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(2) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.downtime.history.ncts.gb.arrivals")
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(2) > td:nth-child(2)")
          .get(0).text() mustBe "Date: 1 January 2022 Time: 10:25am"
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(2) > td:nth-child(3)")
          .get(0).text() mustBe "Date: 2 January 2022 Time: 10:25am"
      }

      "should have rows for XI Departures and Arrivals" in {
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(3) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.downtime.history.ncts.xi.departures")
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(3) > td:nth-child(2)")
          .get(0).text() mustBe "Date: 1 January 2022 Time: 10:25am"
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(3) > td:nth-child(3)")
          .get(0).text() mustBe "Date: 2 January 2022 Time: 10:25am"


        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(4) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.downtime.history.ncts.xi.arrivals")
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(4) > td:nth-child(2)")
          .get(0).text() mustBe "Date: 1 January 2022 Time: 10:25am"
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(4) > td:nth-child(3)")
          .get(0).text() mustBe "Date: 2 January 2022 Time: 10:25am"
      }

      "should have rows for Web and XML channels" in {
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(5) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.downtime.history.ncts.web.channel")
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(5) > td:nth-child(2)")
          .get(0).text() mustBe "Date: 1 January 2022 Time: 10:25am"
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(5) > td:nth-child(3)")
          .get(0).text() mustBe "Date: 2 January 2022 Time: 10:25am"


        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(6) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.downtime.history.ncts.xml.channel")
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(6) > td:nth-child(2)")
          .get(0).text() mustBe "Date: 1 January 2022 Time: 10:25am"
        document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(6) > td:nth-child(3)")
          .get(0).text() mustBe "Date: 2 January 2022 Time: 10:25am"
      }
    }

    "when there is no downtime planned" - {

      "should not have any tables of downtime information" in {
        documentNoDowntime.getElementsByTag("table").size() mustBe 0
      }

      "should have the subheading" in {
        document.select("h2").first().text() mustBe messages("service.downtime.history.h2")
      }

      "should have text about there being no downtime for arrivals" in {
        documentNoDowntime.getElementsByClass("govuk-body").first().text() mustBe messages("service.downtime.history.no.downtime.history")
      }
    }
  }

  val downtimeStartDate: LocalDateTime = LocalDateTime.of(2022, 1, 1, 10, 25, 55)
  val downtimeEndDate: LocalDateTime = LocalDateTime.of(2022, 1, 2, 10, 25, 55)

  val downtime: Downtime = Downtime(
    GBDepartures,
    downtimeStartDate,
    downtimeEndDate
  )

  val downtimeHistory: Seq[Downtime] = Seq(
    downtime,
    downtime.copy(affectedChannel = GBArrivals),
    downtime.copy(affectedChannel = XIDepartures),
    downtime.copy(affectedChannel = XIArrivals),
    downtime.copy(affectedChannel = Web),
    downtime.copy(affectedChannel = XML)
  )
}
