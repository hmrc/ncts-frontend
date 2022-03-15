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
import models.responses.StatusResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Injecting
import utils.DateTimeFormatter
import utils.DateTimeFormatter.formatDateTime
import utils.HealthDetailsExamples._
import views.html.ServiceAvailability

import java.time.LocalDateTime

class ServiceAvailabilityViewSpec extends SpecBase with Injecting {

  val view: ServiceAvailability = inject[ServiceAvailability]
  val transitManualLink = "https://www.gov.uk/government/publications/transit-manual-supplement"

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

    "should have the correct headings" in {
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
        .text() mustBe messages("service.availability.last.message.accepted")

      document.getElementsByClass("govuk-table__cell").first()
        .text() mustBe messages("service.availability.ncts.gb.arrivals")

      document.getElementsByClass("govuk-table__cell").get(2)
        .text() mustBe "12:00am GMT, 24 January 2022"

      document.getElementsByClass("govuk-table__cell").get(3)
        .text() mustBe messages("service.availability.ncts.xi.arrivals")

      document.getElementsByClass("govuk-table__cell").get(5)
        .text() mustBe "12:00am GMT, 24 January 2022"
    }

    "should have a table for departures" in {
      document.getElementsByClass("govuk-table__header").get(3)
        .text() mustBe messages("service.availability.system.core.name")

      document.getElementsByClass("govuk-table__header").get(4)
        .text() mustBe messages("service.availability.system.availability")

      document.getElementsByClass("govuk-table__header").get(5)
        .text() mustBe messages("service.availability.last.message.accepted")

      document.getElementsByClass("govuk-table__cell").get(6)
        .text() mustBe messages("service.availability.ncts.gb.departures")

      document.getElementsByClass("govuk-table__cell").get(8)
        .text() mustBe "12:00am GMT, 24 January 2022"

      document.getElementsByClass("govuk-table__cell").get(9)
        .text() mustBe messages("service.availability.ncts.xi.departures")

      document.getElementsByClass("govuk-table__cell").get(11)
        .text() mustBe "12:00am GMT, 24 January 2022"
    }

    "should have a table for other systems" in {
      document.getElementsByClass("govuk-table__header").get(6)
        .text() mustBe messages("service.availability.channel.name")

      document.getElementsByClass("govuk-table__header").get(7)
        .text() mustBe messages("service.availability.system.availability")

      document.getElementsByClass("govuk-table__cell").get(12)
        .text() mustBe messages("service.availability.submission.channels.status.web.channel")

      document.getElementsByClass("govuk-table__cell").get(14)
        .text() mustBe messages("service.availability.submission.channels.status.xml.channel")
    }

    "should have a last updated time with refresh link for arrivals" in {
      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatDateTime(LocalDateTime.of(2022, 1, 24, 0, 0, 0).minusMinutes(30))}. " +
        s"${messages("service.availability.lastUpdated.refresh")} " +
        s"${messages("service.availability.lastUpdated.latest")}"

      document.getElementsByClass("govuk-inset-text").first()
        .text() mustBe lastUpdatedText
    }

    "should have a last updated time with refresh link for departures" in {
      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatDateTime(LocalDateTime.of(2022, 1, 24, 0, 0, 0).minusSeconds(30))}. " +
        s"${messages("service.availability.lastUpdated.refresh")} " +
        s"${messages("service.availability.lastUpdated.latest")}"

      document.getElementsByClass("govuk-inset-text").get(1)
        .text() mustBe lastUpdatedText
    }

    "should have a last updated time with refresh link for other systems" in {
      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatDateTime(LocalDateTime.of(2022, 1, 24, 0, 0, 0))}. " +
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
        document.getElementsByClass("govuk-table__cell").get(15)
          .text() mustBe messages("service.availability.status.available")
      }

      "should not show PPNS row on the other systems" in {
        document.getElementsByClass("govuk-table").last().text() mustNot
          include(messages("service.availability.submission.channels.status.ppns"))
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
      val allUnhealthyView: Document = Jsoup.parse(view(statusResponse).body)

      "should show that services have known issues for arrivals" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(4)
          .text() mustBe messages("service.availability.status.issues")

        val arrivalsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.arrivals")} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${messages("service.availability.ncts.xi.arrivals")} " +
            s"${messages("service.availability.issues.both.channels")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.gbDeparturesStatus.statusChangedAt)} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)} " +
            s"${messages("service.availability.issues.respectively")} " +
            s"${messages("service.availability.issues.p3")}"

        allUnhealthyView.getElementsByClass("govuk-body").get(0)
          .text() mustBe arrivalsKnownIssuesParagraph
        val bcpPara = allUnhealthyView.getElementsByClass("govuk-body").get(1)
        bcpPara.text() mustBe s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}"
        bcpPara.select("a").attr("href") mustBe transitManualLink
      }

      "should show that services have known issues for departures" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(10)
          .text() mustBe messages("service.availability.status.issues")

        val departuresKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.departures")} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${messages("service.availability.ncts.xi.departures")} " +
            s"${messages("service.availability.issues.both.channels")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.gbDeparturesStatus.statusChangedAt)} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)} " +
            s"${messages("service.availability.issues.respectively")} " +
            s"${messages("service.availability.issues.p3")}"

        allUnhealthyView.getElementsByClass("govuk-body").get(2)
          .text() mustBe departuresKnownIssuesParagraph
        val bcpPara = allUnhealthyView.getElementsByClass("govuk-body").get(3)
        bcpPara.text() mustBe s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}"
        bcpPara.select("a").attr("href") mustBe transitManualLink
      }

      "should show that services have known issues for other systems" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(15)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(17)
          .text() mustBe messages("service.availability.status.issues")

        val ppnsKnownIssuesParagraph =
          messages("service.availability.issues.ppns")
        val channelsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.submission.channels.status.web.channel")} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${messages("service.availability.submission.channels.status.xml.channel")} " +
            s"${messages("service.availability.issues.webAndXML.channel")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.gbDeparturesStatus.statusChangedAt)} " +
            s"${messages("service.availability.issues.p2")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)} " +
            s"${messages("service.availability.issues.respectively")} " +
            s"${messages("service.availability.issues.p3")}"

        allUnhealthyView.getElementsByClass("govuk-body").get(4)
          .text() must include(ppnsKnownIssuesParagraph)
        allUnhealthyView.getElementsByClass("govuk-body").get(5)
          .text() must include(channelsKnownIssuesParagraph)
      }

      "should not have a paragraph about checking third party software for issues" in {
        val thirdPartyMessage = {
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"
        }

        allUnhealthyView.getElementsByClass("govuk-body").last().text() mustNot be(thirdPartyMessage)
      }
    }

    "when GB channels and the Web channel are not healthy" - {
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
      val someUnhealthyView: Document = Jsoup.parse(view(statusResponse).body)

      "should show that the GB channel has known issues for arrivals" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(4)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.issues")

        val arrivalsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.arrivals")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.gbArrivalsStatus.statusChangedAt)}. " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(0)
          .text() mustBe arrivalsKnownIssuesParagraph
        someUnhealthyView.getElementsByClass("govuk-body").get(1)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}"
      }

      "should show that the GB channel has known issues for departures" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(10)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.issues")

        val departuresKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.departures")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.gbDeparturesStatus.statusChangedAt)}. " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(2)
          .text() mustBe departuresKnownIssuesParagraph
        someUnhealthyView.getElementsByClass("govuk-body").get(3)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}"
      }

      "should show that the Web channel has known issues" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(15)
          .text() mustBe messages("service.availability.status.available")

        val webChannelKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.submission.channels.status.web.channel")} " +
            s"${messages("service.availability.issues.webXML.channel")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.webChannelStatus.statusChangedAt)}. " +
            s"${messages("service.availability.issues.p3")}"

        val thirdPartyMessage = {
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"
        }
        someUnhealthyView.getElementsByClass("govuk-body").get(5)
          .text() must include(webChannelKnownIssuesParagraph)
        someUnhealthyView.getElementsByClass("govuk-body").get(6)
          .text() must include(thirdPartyMessage)
      }
    }

    "when XI channels and the XML channel are not healthy" - {
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
      val someUnhealthyView: Document = Jsoup.parse(view(statusResponse).body)

      "should show that the XI channel has known issues for arrivals" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(4)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(15)
          .text() mustBe messages("service.availability.status.issues")

        val arrivalsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.xi.arrivals")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.xiArrivalsStatus.statusChangedAt)}. " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(0)
          .text() mustBe arrivalsKnownIssuesParagraph
        someUnhealthyView.getElementsByClass("govuk-body").get(1)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}"
      }

      "should show that the XI channel has known issues for departures" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(10)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(15)
          .text() mustBe messages("service.availability.status.issues")

        val departuresKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.xi.departures")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)}. " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(2)
          .text() mustBe departuresKnownIssuesParagraph
        someUnhealthyView.getElementsByClass("govuk-body").get(3)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}"
      }

      "should show that the XML channel has known issues" in {
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView.getElementsByClass("govuk-table__cell").get(15)
          .text() mustBe messages("service.availability.status.issues")

        val xmlChannelKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.submission.channels.status.xml.channel")} " +
            s"${messages("service.availability.issues.webXML.channel")} " +
            s"${messages("service.availability.issues.known")} " +
            s"${DateTimeFormatter.formatDateTime(statusResponse.xmlChannelStatus.statusChangedAt)}. " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView.getElementsByClass("govuk-body").get(5)
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

      val ppnsUnhealthyView: Document = Jsoup.parse(view(statusResponse).body)

      "should show PPNS channel has known issues" in {
        ppnsUnhealthyView.getElementsByClass("govuk-table__cell").get(13)
          .text() mustBe messages("service.availability.status.available")
        ppnsUnhealthyView.getElementsByClass("govuk-table__cell").get(15)
          .text() mustBe messages("service.availability.status.available")
        ppnsUnhealthyView.getElementsByClass("govuk-table__cell").get(17)
          .text() mustBe messages("service.availability.status.issues")

        val ppnsKnownIssuesParagraph =
          messages("service.availability.issues.ppns")
        val thirdPartyMessage = {
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"
        }

        ppnsUnhealthyView.getElementsByClass("govuk-body").get(4)
          .text() must include(ppnsKnownIssuesParagraph)
        ppnsUnhealthyView.getElementsByClass("govuk-body").get(5)
          .text() must include(thirdPartyMessage)
      }
    }
  }

  StatusResponse(
    gbDeparturesStatus = healthDetailsHealthy,
    xiDeparturesStatus = healthDetailsHealthy,
    gbArrivalsStatus = healthDetailsHealthy,
    xiArrivalsStatus = healthDetailsHealthy,
    xmlChannelStatus = healthDetailsHealthy,
    webChannelStatus = healthDetailsHealthy,
    ppnStatus = healthDetailsUnhealthy,
    createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
  )

  val document: Document = {

    val lastMessageAccepted = LocalDateTime.of(2022, 1, 24, 0, 0, 0)

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
        createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
      )).body)
  }

}
