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
import models.Channel.{gbArrivals, gbDepartures, xiArrivals, xiDepartures}
import models.responses.StatusResponse
import models.{PlannedDowntime, PlannedDowntimeViewModel}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Injecting
import utils.DateTimeFormatter
import utils.DateTimeFormatter.formatTime
import utils.HealthDetailsExamples._
import views.html.ServiceAvailability

import java.time.{LocalDate, LocalDateTime, LocalTime}

class ServiceAvailabilityViewSpec extends SpecBase with Injecting {

  val view: ServiceAvailability = inject[ServiceAvailability]
  val transitManualLink = "https://www.gov.uk/government/publications/transit-manual-supplement"
  val nctsGuidanceLink = "https://www.gov.uk/guidance/submit-union-transit-declarations-through-ncts"

  "ServiceAvailability" - {

    "should have the correct breadcrumbs" in {
      breadcrumb(0, document).text() mustBe "Home"
      breadcrumb(0, document).attr("href") mustBe govukHomeLink
      breadcrumb(1, document).text() mustBe "NCTS service availability"
      breadcrumb(1, document).attr("href") mustBe homeLink
      breadcrumb(2, document).text() mustBe "Service availability"
      breadcrumb(2, document).attr("href") mustBe serviceAvailabilityLink
    }

    "should have the correct title" in {
      document.title() mustBe "Service availability - NCTS service availability - GOV.UK"
    }

    "should have the correct captions" in {
      document.getElementsByTag("caption").first()
        .text() must (fullyMatch regex s"""${messages("service.availability.status.arrivals")}(.+)""")

      document.getElementsByTag("caption").get(1)
        .text() must (fullyMatch regex s"""${messages("service.availability.status.departures")}(.+)""")

      document.getElementsByTag("caption").get(2)
        .text() must (fullyMatch regex s"""${messages("service.availability.submission.channels.status.heading")}(.+)""")
    }

    "should have a table for arrivals" in {
      document.getElementsByClass("govuk-table__header").first()
        .text() mustBe messages("service.availability.system.core.name")

      document.getElementsByClass("govuk-table__header").get(1)
        .text() mustBe messages("service.availability.system.availability")

      document.getElementsByClass("govuk-table__header").get(2)
        .text() mustBe messages("service.availability.system.known.issues.since")

      document.getElementsByClass("govuk-table__cell").first()
        .text() mustBe messages("service.availability.ncts.gb.arrivals")

      document.getElementsByClass("govuk-table__cell").get(2)
        .text() mustBe messages("service.availability.status.no.issues")

      document.getElementsByClass("govuk-table__cell").get(3)
        .text() mustBe messages("service.availability.ncts.xi.arrivals")

      document.getElementsByClass("govuk-table__cell").get(5)
        .text() mustBe messages("service.availability.status.no.issues")
    }

    "should have a table for departures" in {
      document.getElementsByClass("govuk-table__header").get(3)
        .text() mustBe messages("service.availability.system.core.name")

      document.getElementsByClass("govuk-table__header").get(4)
        .text() mustBe messages("service.availability.system.availability")

      document.getElementsByClass("govuk-table__header").get(5)
        .text() mustBe messages("service.availability.system.known.issues.since")
    }

    "should have a table for other systems" in {
      document.getElementsByClass("govuk-table__header").get(6)
        .text() mustBe messages("service.availability.channel.name")

      document.getElementsByClass("govuk-table__header").get(7)
        .text() mustBe messages("service.availability.system.availability")

      document.getElementsByClass("govuk-table__header").get(8)
        .text() mustBe messages("service.availability.system.known.issues.since")
    }

    "should have a last updated time with refresh link for arrivals" in {
      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatTime(now.minusMinutes(31))}. " +
        s"${messages("service.availability.lastUpdated.refresh")} " +
        s"${messages("service.availability.lastUpdated.latest")}"

      document.getElementsByClass("govuk-inset-text").first()
        .text() mustBe lastUpdatedText
    }

    "should have a last updated time with refresh link for departures" in {
      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatTime(now.minusSeconds(60))}. " +
        s"${messages("service.availability.lastUpdated.refresh")} " +
        s"${messages("service.availability.lastUpdated.latest")}"

      document.getElementsByClass("govuk-inset-text").get(1)
        .text() mustBe lastUpdatedText
    }

    "should have a last updated time with refresh link for other systems" in {

      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatTime(now)}. " +
        s"${messages("service.availability.lastUpdated.refresh")} " +
        s"${messages("service.availability.lastUpdated.latest")}"

      document.getElementsByClass("govuk-inset-text").get(2)
        .text() mustBe lastUpdatedText
    }

    "should have a get help link" in {
      document.body().select(".hmrc-report-technical-issue").first()
        .attr("href") mustBe getHelpUrl
    }

    "should have a related links section with a link to planned downtime" in {
      document.getElementsByTag("h2").first().text() mustBe messages("service.availability.related.links")
      val link = document.select("#main-content > div:nth-child(3) > div > ul > li > a")
      link.text() mustBe messages("service.availability.related.links.planned.downtimes")
      link.attr("href") mustBe "/new-computerised-transit-system-service-availability" +
        "/planned-downtime"
    }

    "when all services are healthy" - {
      "should show that services are available for arrivals" in {
        document.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.available")
        document.getElementsByClass("govuk-table__cell").get(4)
          .text() mustBe messages("service.availability.status.available")
      }

      "should show that services are available for departures" in {
        document.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.available")
        document.getElementsByClass("govuk-table__cell").get(10)
          .text() mustBe messages("service.availability.status.available")
      }

      "should show that services are available for other systems" in {
        document.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.available")
        document.getElementsByClass("govuk-table__cell").get(16)
          .text() mustBe messages("service.availability.status.available")
      }

      "should not show PPNS row on the other systems" in {
        document.getElementsByClass("govuk-table").last().text() mustNot
          include(messages("service.availability.submission.channels.status.ppn"))
      }

      "should have information which helps with channels with no mention of PPNS" in {
        val summaryBlock = document.getElementsByClass("govuk-details__summary").first()
        val detailsBlock = document.getElementsByClass("govuk-details__text").first()

        summaryBlock.getElementsByClass("govuk-details__summary-text")
          .first().text() mustBe messages("service.availability.help.with.channels")

        detailsBlock.getElementsByClass("govuk-body")
          .first().text() mustBe messages("service.availability.help.with.channels.p1")

        val para2WithLink = detailsBlock.getElementsByClass("govuk-body")

        para2WithLink.get(1).text() mustBe s"${messages("service.availability.help.with.channels.p2a")}" +
        s" ${messages("service.availability.help.with.channels.p2b")}" +
        s" ${messages("service.availability.help.with.channels.p2c")}"

        para2WithLink.select("a").attr("href") mustBe nctsGuidanceLink

        detailsBlock.getElementsByClass("govuk-body")
          .get(2).text() mustBe messages("service.availability.help.with.channels.p3")

        detailsBlock.getElementsByClass("govuk-body").size() mustBe 3
      }

      "should have a paragraph about checking third party software for issues" in {
        val thirdPartyMessage = {
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"
        }

        document.getElementsByClass("govuk-body").last().text() mustBe thirdPartyMessage
      }
    }

    "when all services are unhealthy" - {
      val statusResponse = StatusResponse(
        gbDeparturesStatus = healthDetailsUnhealthy,
        xiDeparturesStatus = healthDetailsUnhealthy,
        gbArrivalsStatus = healthDetailsUnhealthy,
        xiArrivalsStatus = healthDetailsUnhealthy,
        xmlChannelStatus = healthDetailsUnhealthy,
        webChannelStatus = healthDetailsUnhealthy,
        ppnStatus = healthDetailsUnhealthy,
        createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
      )
      val allUnhealthyView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

      "should show that services have known issues and known issues since time for arrivals" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(2)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.gbArrivalsStatus.statusChangedAt)
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(4)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(5)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.xiArrivalsStatus.statusChangedAt)

        val arrivalsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.arrivals")} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${messages("service.availability.ncts.xi.arrivals")} " +
            s"${messages("service.availability.issues.both.channels")} " +
            s"${messages("service.availability.issues.p3")}"

        allUnhealthyView.getElementsByClass("govuk-body").get(0)
          .text() mustBe arrivalsKnownIssuesParagraph
        val bcpPara = allUnhealthyView.getElementsByClass("govuk-body").get(1)
        bcpPara.text() mustBe s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
        bcpPara.select("a").attr("href") mustBe transitManualLink
      }

      "should show that services have known issues and known issues since time for departures" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(8)
          .text() mustBe  DateTimeFormatter.formatDateTime(statusResponse.gbDeparturesStatus.statusChangedAt)
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(10)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(11)
          .text() mustBe  DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)

        val departuresKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.departures")} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${messages("service.availability.ncts.xi.departures")} " +
            s"${messages("service.availability.issues.both.channels")} " +
            s"${messages("service.availability.issues.p3")}"

        allUnhealthyView.getElementsByClass("govuk-body").get(2)
          .text() mustBe departuresKnownIssuesParagraph
        val bcpPara = allUnhealthyView.getElementsByClass("govuk-body").get(3)
        bcpPara.text() mustBe s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
        bcpPara.select("a").attr("href") mustBe transitManualLink
      }

      "should show that services have known issues and known issues since time for other systems" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(14)
          .text() mustBe  DateTimeFormatter.formatDateTime(statusResponse.webChannelStatus.statusChangedAt)
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(16)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(17)
          .text() mustBe  DateTimeFormatter.formatDateTime(statusResponse.xmlChannelStatus.statusChangedAt)
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(19)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(20)
          .text() mustBe  DateTimeFormatter.formatDateTime(statusResponse.ppnStatus.statusChangedAt)

        val ppnKnownIssuesParagraph =
          messages("service.availability.issues.ppn")

        val channelsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.submission.channels.status.web.channel")} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${messages("service.availability.submission.channels.status.xml.channel")} " +
            s"${messages("service.availability.issues.webAndXML.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        allUnhealthyView.getElementsByClass("govuk-body").get(8)
          .text() must include(ppnKnownIssuesParagraph)
        allUnhealthyView.getElementsByClass("govuk-body").get(9)
          .text() must include(channelsKnownIssuesParagraph)
      }

      "should have information which helps with channels with mention of PPNS" in {
        val summaryBlock = allUnhealthyView.getElementsByClass("govuk-details__summary").first()
        val detailsBlock = allUnhealthyView.getElementsByClass("govuk-details__text").first()

        summaryBlock.getElementsByClass("govuk-details__summary-text")
          .first().text() mustBe messages("service.availability.help.with.channels")

        detailsBlock.getElementsByClass("govuk-body")
          .first().text() mustBe messages("service.availability.help.with.channels.p1")

        detailsBlock.getElementsByClass("govuk-body")
          .get(1).text() mustBe s"${messages("service.availability.help.with.channels.p2a")}" +
          s" ${messages("service.availability.help.with.channels.p2b")}" +
          s" ${messages("service.availability.help.with.channels.p2c")}"

        detailsBlock.getElementsByClass("govuk-body")
          .get(2).text() mustBe messages("service.availability.help.with.channels.p3")

        detailsBlock.getElementsByClass("govuk-body")
          .get(3).text() mustBe messages("service.availability.help.with.channels.p4")

        detailsBlock.getElementsByClass("govuk-body").size() mustBe 4
      }

      "should not have a paragraph about checking third party software for issues" in {
        val thirdPartyMessage = {
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"
        }

        allUnhealthyView.getElementsByClass("govuk-body").last().text() mustNot be(thirdPartyMessage)
      }
    }

    "when GB departures and arrivals and the Web channel are unhealthy but XI departures and arrivals are healthy" - {
      val statusResponse = StatusResponse(
        gbDeparturesStatus = healthDetailsUnhealthy,
        xiDeparturesStatus = healthDetailsHealthy,
        gbArrivalsStatus = healthDetailsUnhealthy,
        xiArrivalsStatus = healthDetailsHealthy,
        xmlChannelStatus = healthDetailsHealthy,
        webChannelStatus = healthDetailsUnhealthy,
        ppnStatus = healthDetailsUnhealthy,
        createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
      )

      val someUnhealthyView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

      "should show that GB arrivals has known issues" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(4)
          .text() mustBe messages("service.availability.status.available")

        val arrivalsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.arrivals")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(0)
          .text() mustBe arrivalsKnownIssuesParagraph
        someUnhealthyView.getElementsByClass("govuk-body").get(1)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
      }


      "should show that the GB departures has known issues" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(10)
          .text() mustBe messages("service.availability.status.available")

        val departuresKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.departures")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(2)
          .text() mustBe departuresKnownIssuesParagraph
        someUnhealthyView.getElementsByClass("govuk-body").get(3)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
      }

      "should show that the Web channel has known issues" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(16)
          .text() mustBe messages("service.availability.status.available")

        val webChannelKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.submission.channels.status.web.channel")} " +
            s"${messages("service.availability.issues.webXML.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        val thirdPartyMessage = {
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"
        }
        someUnhealthyView.getElementsByClass("govuk-body").get(9)
          .text() must include(webChannelKnownIssuesParagraph)
        someUnhealthyView.getElementsByClass("govuk-body").get(10)
          .text() must include(thirdPartyMessage)
      }
    }

    "when XI departures and arrivals and the XML channel are unhealthy but GB departures and arrivals are healthy" - {
      val statusResponse = StatusResponse(
        gbDeparturesStatus = healthDetailsHealthy,
        xiDeparturesStatus = healthDetailsUnhealthy,
        gbArrivalsStatus = healthDetailsHealthy,
        xiArrivalsStatus = healthDetailsUnhealthy,
        xmlChannelStatus = healthDetailsUnhealthy,
        webChannelStatus = healthDetailsHealthy,
        ppnStatus = healthDetailsUnhealthy,
        createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
      )
      val someUnhealthyView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

      "should show that XI arrivals has known issues" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(4)
          .text() mustBe messages("service.availability.status.issues")

        val arrivalsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.xi.arrivals")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(0)
          .text() mustBe arrivalsKnownIssuesParagraph
        someUnhealthyView.getElementsByClass("govuk-body").get(1)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
      }

      "should show that the XI channel has known issues for departures" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(10)
          .text() mustBe messages("service.availability.status.issues")

        val departuresKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.xi.departures")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(2)
          .text() mustBe departuresKnownIssuesParagraph
        someUnhealthyView.getElementsByClass("govuk-body").get(3)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
      }

      "should show that the XML channel has known issues" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(16)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(17)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.xmlChannelStatus.statusChangedAt)

        val xmlChannelKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.submission.channels.status.xml.channel")} " +
            s"${messages("service.availability.issues.webXML.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(9)
          .text() must include(xmlChannelKnownIssuesParagraph)
      }
    }

    "when PPNS is not healthy" - {
      val statusResponse = StatusResponse(
        gbDeparturesStatus = healthDetailsHealthy,
        xiDeparturesStatus = healthDetailsUnhealthy,
        gbArrivalsStatus = healthDetailsHealthy,
        xiArrivalsStatus = healthDetailsUnhealthy,
        xmlChannelStatus = healthDetailsHealthy,
        webChannelStatus = healthDetailsHealthy,
        ppnStatus = healthDetailsUnhealthy,
        createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
      )

      val ppnUnhealthyView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

      "should show PPNS channel has known issues" in {
        ppnUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.available")
        ppnUnhealthyView.getElementsByClass("govuk-table__cell").get(16)
          .text() mustBe messages("service.availability.status.available")
        ppnUnhealthyView.getElementsByClass("govuk-table__cell").get(19)
          .text() mustBe messages("service.availability.status.issues")
        ppnUnhealthyView.getElementsByClass("govuk-table__cell").get(20)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.ppnStatus.statusChangedAt)

        val ppnKnownIssuesParagraph =
          messages("service.availability.issues.ppn")
        val thirdPartyMessage = {
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"
        }

        ppnUnhealthyView.getElementsByClass("govuk-body").get(8)
          .text() must include(ppnKnownIssuesParagraph)
        ppnUnhealthyView.getElementsByClass("govuk-body").get(9)
          .text() must include(thirdPartyMessage)
      }
    }

    "when there is PlannedDowntime" -{
      "should show planned downtime for the availability status" in {
        val date = LocalDate.now()
        val time = LocalTime.now()

        val statusResponse =   StatusResponse(
          gbDeparturesStatus = healthDetailsHealthy,
          xiDeparturesStatus = healthDetailsHealthy,
          gbArrivalsStatus = healthDetailsHealthy,
          xiArrivalsStatus = healthDetailsHealthy,
          xmlChannelStatus = healthDetailsHealthy,
          webChannelStatus = healthDetailsHealthy,
          ppnStatus = healthDetailsHealthy,
          createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
        )

        val plannedDowntimeViewModel = PlannedDowntimeViewModel(
          Some(PlannedDowntime(date, time.minusMinutes(1), date, time.plusMinutes(1), gbArrivals)),
          Some(PlannedDowntime(date, time.minusMinutes(1), date, time.plusMinutes(1), xiArrivals)),
          Some(PlannedDowntime(date, time.minusMinutes(1), date, time.plusMinutes(1), gbDepartures)),
          Some(PlannedDowntime(date, time.minusMinutes(1), date, time.plusMinutes(1), xiDepartures))
        )

        val allHealthyWithPlannedDowntime: Document = Jsoup.parse(view(statusResponse, plannedDowntimeViewModel).body)

        allHealthyWithPlannedDowntime.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.planned.downtime")
        allHealthyWithPlannedDowntime.getElementsByClass("govuk-table__cell").get(2)
          .text() mustBe messages("service.availability.status.no.issues")
        allHealthyWithPlannedDowntime.getElementsByClass("govuk-table__cell").get(4)
          .text() mustBe messages("service.availability.status.planned.downtime")
        allHealthyWithPlannedDowntime.getElementsByClass("govuk-table__cell").get(5)
          .text() mustBe messages("service.availability.status.no.issues")
        allHealthyWithPlannedDowntime.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.planned.downtime")
        allHealthyWithPlannedDowntime.getElementsByClass("govuk-table__cell").get(8)
          .text() mustBe messages("service.availability.status.no.issues")
        allHealthyWithPlannedDowntime.getElementsByClass("govuk-table__cell").get(10)
          .text() mustBe messages("service.availability.status.planned.downtime")
        allHealthyWithPlannedDowntime.getElementsByClass("govuk-table__cell").get(11)
          .text() mustBe messages("service.availability.status.no.issues")
      }
    }
  }

  val now: LocalDateTime = LocalDateTime.now

  val document: Document = {
    
    val lastMessageAccepted = now

    val healthDetails = healthDetailsHealthy.copy(lastMessageAccepted = Some(lastMessageAccepted))

    Jsoup.parse(view(
      StatusResponse(
        gbDeparturesStatus = healthDetails,
        xiDeparturesStatus = healthDetails,
        gbArrivalsStatus = healthDetails,
        xiArrivalsStatus = healthDetails,
        xmlChannelStatus = healthDetails,
        webChannelStatus = healthDetails,
        ppnStatus = healthDetails,
        createdTs = now
      ), PlannedDowntimeViewModel.default).body)
  }
}
