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
import models.{GBArrivals, GBDepartures, PlannedDowntime, PlannedDowntimes, XIArrivals, XIDepartures}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Injecting
import views.html.{PlannedDowntime => PlannedDowntimeView}

import java.time.{LocalDate, LocalTime}

class PlannedDowntimeViewSpec extends SpecBase with Injecting {

  "PlannedDowntimeView" - {

    def view: PlannedDowntimeView         = inject[PlannedDowntimeView]
    lazy val document: Document           = Jsoup.parse(view(Some(plannedDowntimes)).body)
    lazy val documentNoDowntime: Document = Jsoup.parse(view(None).body)

    "should have the correct breadcrumbs" in {
      breadcrumb(0, document).text() mustBe "Home"
      breadcrumb(0, document).attr("href") mustBe govukHomeLink
      breadcrumb(1, document).text() mustBe "NCTS service availability"
      breadcrumb(1, document).attr("href") mustBe homeLink
      breadcrumb(2, document).text() mustBe "Planned downtime"
      breadcrumb(2, document).attr("href") mustBe plannedDowntimeLink
    }

    "should have the correct heading" in {
      document.select("h1").first().text() mustBe messages("planned-downtime.heading")
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

    "when there is downtime planned" - {

      "should have the correct date message" in {
        documentNoDowntime.select("#main-content > p:nth-child(2)").text() mustBe messages("planned-downtime.temp.p1")
      }

      "should have the correct subheading" in {
        documentNoDowntime.select("#main-content > h2").text() mustBe messages("planned-downtime.temp.h2")
      }

      "should have the correct first paragraph" in {
        documentNoDowntime.select("#main-content > p:nth-child(4)").text() mustBe messages("planned-downtime.temp.p2")
      }

      "should have the correct second paragraph" in {
        documentNoDowntime.select("#main-content > p:nth-child(5)").text() mustBe messages("planned-downtime.temp.p3")
      }
    }
  }

  val downtimeStartDate: LocalDate = LocalDate.of(2022, 1, 1)
  val downtimeEndDate: LocalDate   = LocalDate.of(2022, 6, 1)
  val downtimeStartTime: LocalTime = LocalTime.of(8, 0)
  val downtimeEndTime: LocalTime   = LocalTime.of(20, 0)

  val gbDeparturesPlannedDowntime: models.PlannedDowntime = PlannedDowntime(
    downtimeStartDate,
    downtimeStartTime,
    downtimeEndDate,
    downtimeEndTime,
    GBDepartures
  )

  val plannedDowntimes: PlannedDowntimes = PlannedDowntimes(
    Seq(
      gbDeparturesPlannedDowntime,
      gbDeparturesPlannedDowntime.copy(affectedChannel = GBArrivals),
      gbDeparturesPlannedDowntime.copy(affectedChannel = XIDepartures),
      gbDeparturesPlannedDowntime.copy(affectedChannel = XIArrivals)
    )
  )

}
