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
import models.{Channel, PlannedDowntime, PlannedDowntimes}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Injecting
import views.html.{PlannedDowntime => PlannedDowntimeView}

import java.time.{LocalDate, LocalTime}

class PlannedDowntimeViewSpec extends SpecBase with Injecting {

  "PlannedDowntimeView" - {

    def view: PlannedDowntimeView = inject[PlannedDowntimeView]
    lazy val document: Document = Jsoup.parse(view(Some(plannedDowntimes)).body)
    lazy val documentNoDowntime: Document = Jsoup.parse(view(None).body)

    "should have the correct breadcrumbs" in {
      breadcrumb(0, document).text() mustBe "Home"
      breadcrumb(0, document).attr("href") mustBe govukHomeLink
      breadcrumb(1, document).text() mustBe "Service availability and Planned downtime"
      breadcrumb(1, document).attr("href") mustBe homeLink
      breadcrumb(2, document).text() mustBe "Planned downtime"
      breadcrumb(2, document).attr("href") mustBe plannedDowntimeLink
    }

    "should have the correct heading" in {
      document.select("h1").first().text() mustBe messages("service.planned-downtime.heading")
    }

    "should have a get help link" in {
      document.body().select(".hmrc-report-technical-issue").first()
        .attr("href") mustBe getHelpUrl
    }

    "when there is downtime planned for arrivals" - {

      "should have the arrivals subheading" in {
        document.select("h2").first().text() mustBe messages("service.planned-downtime.ncts.arrivals")
      }

      "should have a table for arrivals with the correct heading" in {
        document.select("table:nth-child(2) > thead > tr > th:nth-child(1)").get(0).text() mustBe messages("service.planned-downtime.system.name")
        document.select("table:nth-child(2) > thead > tr > th:nth-child(2)").get(0).text() mustBe messages("service.planned-downtime.start")
        document.select("table:nth-child(2) > thead > tr > th:nth-child(3)").get(0).text() mustBe messages("service.planned-downtime.end")
      }

      "should have a table for arrivals with a row for GB" in {
        document.select("table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.planned-downtime.ncts.gb.arrivals")

        document.select("table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(2)")
          .get(0).text() mustBe "Date: Saturday 1 January 2022 Time: 8AM"

        document.select("table:nth-child(2) > tbody > tr:nth-child(1) > td:nth-child(3)")
          .get(0).text() mustBe "Date: Wednesday 1 June 2022 Time: 8PM"
      }

      "should have a table for arrivals with a row for XI" in {
        document.select("table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.planned-downtime.ncts.xi.arrivals")

        document.select("table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(2)")
          .get(0).text() mustBe "Date: Saturday 1 January 2022 Time: 8AM"

        document.select("table:nth-child(2) > tbody > tr:nth-child(2) > td:nth-child(3)")
          .get(0).text() mustBe "Date: Wednesday 1 June 2022 Time: 8PM"
      }

      "should have some content about submissions when the service is down and an apology for arrivals" in {
        document.getElementsByClass("govuk-body").first().text() mustBe messages("service.planned-downtime.p1")
        document.getElementsByClass("govuk-body").get(1).text() mustBe messages("service.planned-downtime.p2")
      }

      "should have a related links section with a link to service availability" in {
        document.getElementsByTag("h2").get(2).text() mustBe messages("service.availability.related.links")
        val link = document.select("#main-content > div:nth-child(3) > div > ul > li > a")
        link.text() mustBe messages("service.planned-downtime.related.links.service.availability")
        link.attr("href") mustBe "/new-computerised-transit-system-service-availability-and-issues" +
          "/service-availability"
      }
    }

    "when there is downtime planned for departures" - {

      "should have the departures subheading" in {
        document.select("h2").get(1).text() mustBe messages("service.planned-downtime.ncts.departures")
      }

      "should have a table for departures with the correct heading" in {
        val table = document.select("table:nth-child(6) > thead > tr")
        table.select("th:nth-child(1)").get(0).text() mustBe messages("service.planned-downtime.system.name")
        table.select("th:nth-child(2)").get(0).text() mustBe messages("service.planned-downtime.start")
        table.select("th:nth-child(3)").get(0).text() mustBe messages("service.planned-downtime.end")
      }

      "should have a table for departures with a row for GB" in {
        document.select("table:nth-child(6) > tbody > tr:nth-child(1) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.planned-downtime.ncts.gb.departures")

        document.select("table:nth-child(6) > tbody > tr:nth-child(1) > td:nth-child(2)")
          .get(0).text() mustBe "Date: Saturday 1 January 2022 Time: 8AM"

        document.select("table:nth-child(6) > tbody > tr:nth-child(1) > td:nth-child(3)")
          .get(0).text() mustBe "Date: Wednesday 1 June 2022 Time: 8PM"
      }

      "should have a table for departures with a row for XI" in {
        document.select("table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(1)")
          .get(0).text() mustBe messages("service.planned-downtime.ncts.xi.departures")

        document.select("table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(2)")
          .get(0).text() mustBe "Date: Saturday 1 January 2022 Time: 8AM"

        document.select("table:nth-child(6) > tbody > tr:nth-child(2) > td:nth-child(3)")
          .get(0).text() mustBe "Date: Wednesday 1 June 2022 Time: 8PM"
      }

      "should have some content about submissions when the service is down and an apology for departures" in {
        document.getElementsByClass("govuk-body").get(2).text() mustBe messages("service.planned-downtime.p1")
        document.getElementsByClass("govuk-body").last().text() mustBe messages("service.planned-downtime.p2")
      }
    }

    "when there is no downtime planned" - {

      "should not have any tables of downtime information" in {
        documentNoDowntime.getElementsByTag("table").size() mustBe 0
      }

      "should have the arrivals subheading" in {
        documentNoDowntime.select("h2").first().text() mustBe messages("service.planned-downtime.ncts.arrivals")
      }

      "should have text about there being no downtime for arrivals" in {
        documentNoDowntime.getElementsByClass("govuk-body").first().text() mustBe messages("service.planned-downtime.no.downtime.planned.arrivals")
      }

      "should have the departures subheading" in {
        documentNoDowntime.select("h2").get(1).text() mustBe messages("service.planned-downtime.ncts.departures")
      }

      "should have text about there being no downtime for departures" in {
        documentNoDowntime.getElementsByClass("govuk-body").last().text() mustBe messages("service.planned-downtime.no.downtime.planned.departures")
      }
    }
  }

  val downtimeStartDate: LocalDate = LocalDate.of(2022, 1, 1)
  val downtimeEndDate: LocalDate = LocalDate.of(2022, 6, 1)
  val downtimeStartTime: LocalTime = LocalTime.of(8,0)
  val downtimeEndTime: LocalTime = LocalTime.of(20,0)

  val gbDeparturesPlannedDowntime: models.PlannedDowntime = PlannedDowntime(
    downtimeStartDate,
    downtimeStartTime,
    downtimeEndDate,
    downtimeEndTime,
    Channel.gbDepartures
  )

  val plannedDowntimes: PlannedDowntimes = PlannedDowntimes(Seq(
    gbDeparturesPlannedDowntime,
    gbDeparturesPlannedDowntime.copy(affectedChannel = Channel.gbArrivals),
    gbDeparturesPlannedDowntime.copy(affectedChannel = Channel.xiDepartures),
    gbDeparturesPlannedDowntime.copy(affectedChannel = Channel.xiArrivals)
  ))

}
