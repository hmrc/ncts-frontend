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

    lazy val document: Document           = Jsoup.parse(view(downtimeHistory).body)
    lazy val documentNoDowntime: Document = Jsoup.parse(view(Seq.empty).body)

    "should have the correct breadcrumbs" in {
      breadcrumb(0, document).text() mustBe "Home"
      breadcrumb(0, document).attr("href") mustBe govukHomeLink
      breadcrumb(1, document).text() mustBe "NCTS service availability"
      breadcrumb(1, document).attr("href") mustBe homeLink
      breadcrumb(2, document).text() mustBe "Downtime history"
      breadcrumb(2, document).attr("href") mustBe downtimeHistoryLink
    }

    "should have the correct heading" in {
      document.select("h1").first().text() mustBe messages("service.downtime.history.heading")
    }

    "should have an exit survey" - {

      "with the correct heading" in {
        document.select("#exit-survey > h2").text() mustBe messages("exitSurvey.heading")
      }

      "with the correct first paragraph" in {
        document.select("#exit-survey > p:nth-of-type(1)").text() mustBe messages("exitSurvey.p1")
      }

      "with the correct second paragraph" in {
        document.select("#exit-survey > p:nth-of-type(2)").text() mustBe
          messages("exitSurvey.link") + " " + messages("exitSurvey.p2")
      }

      "with the correct link" in {
        document.select("#exit-survey > p > a").attr("href") mustBe feedbackFrontendUrl
      }
    }

    "should have a get help link" in {
      document
        .body()
        .select(".hmrc-report-technical-issue")
        .first()
        .attr("href") mustBe getHelpUrl
    }

    "should have a related links section with a link to service availability" in {
      document.getElementsByTag("h2").get(1).text() mustBe messages("service.availability.related.links")
      val link = document.select("#main-content > div:nth-child(3) > div > ul > li > a")

      link.first().text() mustBe messages("service.downtime.history.related.links.service.availability")
      link.first().attr("href") mustBe "/new-computerised-transit-system-service-availability" +
        "/service-availability"
    }

    "should have a related links section with a link to planned downtime" in {
      document.getElementsByTag("h2").get(1).text() mustBe messages("service.availability.related.links")
      val link = document.select("#main-content > div:nth-child(3) > div > ul > li > a")

      link.get(1).text() mustBe messages("service.downtime.history.related.links.planned.downtime")
      link.get(1).attr("href") mustBe "/new-computerised-transit-system-service-availability" +
        "/planned-downtime"
    }

    "when there are downtime history" - {

      "should have the subheading" in {
        document.select("h2").first().text() mustBe messages("service.downtime.history.h2")
      }

      "should have a table for downtime history with the correct heading" in {
        document.select("div:nth-child(2) > div > table > thead > tr > th:nth-child(1)").get(0).text() mustBe messages(
          "service.downtime.history.component.name"
        )
        document.select("div:nth-child(2) > div > table > thead > tr > th:nth-child(2)").get(0).text() mustBe messages(
          "service.downtime.history.event.type"
        )
        document.select("div:nth-child(2) > div > table > thead > tr > th:nth-child(3)").get(0).text() mustBe messages(
          "service.downtime.history.start"
        )
        document.select("div:nth-child(2) > div > table > thead > tr > th:nth-child(4)").get(0).text() mustBe messages(
          "service.downtime.history.end"
        )
      }

      "should have rows for GB Departures and Arrivals" in {
        val gbDepartureRow = document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(1)")

        gbDepartureRow
          .select("td:nth-child(1)")
          .get(0)
          .text() mustBe messages("service.downtime.history.ncts.gb.departures")
        gbDepartureRow
          .select("td:nth-child(2)")
          .get(0)
          .text() mustBe messages("service.downtime.history.unplanned.event")
        gbDepartureRow
          .select("td:nth-child(3)")
          .get(0)
          .text() mustBe "Date: 1 January 2022 Time: 10:25am GMT"
        gbDepartureRow
          .select("td:nth-child(4)")
          .get(0)
          .text() mustBe "Date: 2 January 2022 Time: 10:25am GMT"

        val gbArrivalRow = document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(2)")

        gbArrivalRow
          .select("td:nth-child(1)")
          .get(0)
          .text() mustBe messages("service.downtime.history.ncts.gb.arrivals")
        gbDepartureRow
          .select("td:nth-child(2)")
          .get(0)
          .text() mustBe messages("service.downtime.history.unplanned.event")
        gbArrivalRow
          .select("td:nth-child(3)")
          .get(0)
          .text() mustBe "Date: 1 January 2022 Time: 10:25am GMT"
        gbArrivalRow
          .select("td:nth-child(4)")
          .get(0)
          .text() mustBe "Date: 2 January 2022 Time: 10:25am GMT"
      }

      "should have rows for XI Departures and Arrivals" in {
        val xiDepartureRow = document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(3)")

        xiDepartureRow
          .select("td:nth-child(1)")
          .get(0)
          .text() mustBe messages("service.downtime.history.ncts.xi.departures")
        xiDepartureRow
          .select("td:nth-child(2)")
          .get(0)
          .text() mustBe messages("service.downtime.history.unplanned.event")
        xiDepartureRow
          .select("td:nth-child(3)")
          .get(0)
          .text() mustBe "Date: 1 January 2022 Time: 10:25am GMT"
        xiDepartureRow
          .select("td:nth-child(4)")
          .get(0)
          .text() mustBe "Date: 2 January 2022 Time: 10:25am GMT"

        val xiArrivalRow = document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(4)")

        xiArrivalRow
          .select("td:nth-child(1)")
          .get(0)
          .text() mustBe messages("service.downtime.history.ncts.xi.arrivals")
        xiArrivalRow
          .select("td:nth-child(2)")
          .get(0)
          .text() mustBe messages("service.downtime.history.unplanned.event")
        xiArrivalRow
          .select("td:nth-child(3)")
          .get(0)
          .text() mustBe "Date: 1 January 2022 Time: 10:25am GMT"
        xiArrivalRow
          .select("td:nth-child(4)")
          .get(0)
          .text() mustBe "Date: 2 January 2022 Time: 10:25am GMT"
      }

      "should have rows for Web and XML channels" in {
        val webChannelRow = document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(5)")

        webChannelRow
          .select("td:nth-child(1)")
          .get(0)
          .text() mustBe messages("service.downtime.history.ncts.web.channel")
        webChannelRow
          .select("td:nth-child(2)")
          .get(0)
          .text() mustBe messages("service.downtime.history.unplanned.event")
        webChannelRow
          .select("td:nth-child(3)")
          .get(0)
          .text() mustBe "Date: 1 January 2022 Time: 10:25am GMT"
        webChannelRow
          .select(" td:nth-child(4)")
          .get(0)
          .text() mustBe "Date: 2 January 2022 Time: 10:25am GMT"

        val xmlChannelRow = document.select("div:nth-child(2) > div > table > tbody > tr:nth-child(6)")

        xmlChannelRow
          .select("td:nth-child(1)")
          .get(0)
          .text() mustBe messages("service.downtime.history.ncts.xml.channel")
        xmlChannelRow
          .select("td:nth-child(2)")
          .get(0)
          .text() mustBe messages("service.downtime.history.unplanned.event")
        xmlChannelRow
          .select("td:nth-child(3)")
          .get(0)
          .text() mustBe "Date: 1 January 2022 Time: 10:25am GMT"
        xmlChannelRow
          .select("td:nth-child(4)")
          .get(0)
          .text() mustBe "Date: 2 January 2022 Time: 10:25am GMT"
      }
    }

    "when there is no downtime history" - {

      "should not have any tables of downtime information" in {
        documentNoDowntime.getElementsByTag("table").size() mustBe 0
      }

      "should have the subheading" in {
        document.select("h2").first().text() mustBe messages("service.downtime.history.h2")
      }

      "should have text about there being no downtime" in {
        documentNoDowntime.getElementsByClass("govuk-body").first().text() mustBe messages(
          "service.downtime.history.no.downtime.history"
        )
      }
    }
  }

  val downtimeStartDate: LocalDateTime = LocalDateTime.of(2022, 1, 1, 10, 25, 55)
  val downtimeEndDate: LocalDateTime   = LocalDateTime.of(2022, 1, 2, 10, 25, 55)

  val downtime: Downtime = Downtime(
    GBDepartures,
    downtimeStartDate,
    downtimeEndDate
  )

  val downtimeHistory: Seq[DowntimeHistoryRow] = Seq(
    DowntimeHistoryRow(downtime, planned = false),
    DowntimeHistoryRow(downtime.copy(affectedChannel = GBArrivals), planned = false),
    DowntimeHistoryRow(downtime.copy(affectedChannel = XIDepartures), planned = false),
    DowntimeHistoryRow(downtime.copy(affectedChannel = XIArrivals), planned = false),
    DowntimeHistoryRow(downtime.copy(affectedChannel = Web), planned = false),
    DowntimeHistoryRow(downtime.copy(affectedChannel = XML), planned = false)
  )
}
