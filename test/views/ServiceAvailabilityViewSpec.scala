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
import models.responses.{StatusResponse, TimelineUpdate}
import models.{Channel, GBArrivals, GBDepartures, PPN, PlannedDowntime, PlannedDowntimeViewModel, Web, XIArrivals, XIDepartures, XML}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Injecting
import utils.DateTimeFormatter
import utils.DateTimeFormatter.formatTime
import utils.HealthDetailsExamples._
import views.html.ServiceAvailability

import java.time.{LocalDate, LocalDateTime, ZoneId, ZonedDateTime}

class ServiceAvailabilityViewSpec extends SpecBase with Injecting {

  val view: ServiceAvailability = inject[ServiceAvailability]
  val transitManualLink         = "https://www.gov.uk/government/publications/transit-manual-supplement"
  val nctsGuidanceLink          = "https://www.gov.uk/guidance/submit-union-transit-declarations-through-ncts"

  val now                           = ZonedDateTime.now(ZoneId.of("Europe/London")).toLocalDateTime
  val date                          = now.toLocalDate
  val time                          = now.toLocalTime
  val allChannelsHealthyWithoutEtas = StatusResponse(
    gbDeparturesStatus = healthDetailsHealthy,
    xiDeparturesStatus = healthDetailsHealthy,
    gbArrivalsStatus = healthDetailsHealthy,
    xiArrivalsStatus = healthDetailsHealthy,
    xmlChannelStatus = healthDetailsHealthy,
    webChannelStatus = healthDetailsHealthy,
    ppnStatus = healthDetailsHealthy,
    createdTs = now
  )
  val twentyMinutesAgo              = LocalDateTime.now.minusMinutes(20)
  val tenMinutesAgo                 = LocalDateTime.now.minusMinutes(10)
  val fiveMinutesAgo                = LocalDateTime.now.minusMinutes(5)
  val twoMinutesAgo                 = LocalDateTime.now.minusMinutes(2)
  val fewSecondsAgo                 = LocalDateTime.now.minusSeconds(15)
  val etaTime                       = "10am BST"
  val etaDate: LocalDate            = LocalDate.now()

  def eta(ch: Channel, createdTs: LocalDateTime, isBCP: Boolean = false) =
    TimelineUpdate(ch, Option(etaTime), Option(etaDate), businessContinuityFlag = isBCP, createdTs)
  def bcpWithoutEta(ch: Channel, createdTs: LocalDateTime)               =
    TimelineUpdate(ch, None, None, businessContinuityFlag = true, createdTs)
  def bcpWithEta(ch: Channel, createdTs: LocalDateTime)                  =
    TimelineUpdate(ch, Option(etaTime), Option(etaDate), businessContinuityFlag = true, createdTs)

  case class TimeLine(caption: String, time: String, message: String, bcpLink: String)

  def timeLineContent(view: Document, eventIndex: Int): TimeLine = {
    val events  = view.getElementsByClass("hmrc-timeline__event")
    val event   = events.get(eventIndex)
    val caption = event.getElementsByClass("hmrc-timeline__event-title govuk-table__caption--s").text()
    val time    = event.getElementsByTag("span").text()
    val message = event.getElementsByClass("hmrc-timeline__event-content").text()
    val bcpLink = event.select("a").attr("href")
    TimeLine(caption, time, message, bcpLink)
  }

  def checkGbOrXiIssueContent(timelineEvent: TimeLine, channel: String) = {
    timelineEvent.caption mustBe
      s"""${messages("service.availability.status.issues")} - ${messages(s"service.availability.ncts.$channel")}"""
    timelineEvent.message mustBe (s"${messages("service.availability.issues.p1")} " +
      s"${messages(s"service.availability.ncts.$channel")} " +
      s"${messages("service.availability.issues.single.channel")} " +
      s"${messages("service.availability.issues.p3")} " +
      s"${messages("service.availability.issues.p4")} " +
      s"${messages("service.availability.issues.p5")}.")
    timelineEvent.bcpLink mustBe transitManualLink
  }

  def checkWebOrXmlIssueContent(timelineEvent: TimeLine, channel: String) = {
    timelineEvent.caption mustBe
      s"""${messages("service.availability.status.issues")} - ${messages(
          s"service.availability.timeline.channel.caption.$channel"
        )}"""
    timelineEvent.message mustBe (s"${messages("service.availability.issues.p1")} " +
      s"${messages(s"service.availability.submission.channels.status.$channel")} " +
      s"${messages("service.availability.issues.webXML.channel")} " +
      s"${messages("service.availability.issues.p3")}")
  }

  def checkPpnIssueContent(timelineEvent: TimeLine, channel: String) = {
    timelineEvent.caption mustBe
      s"""${messages("service.availability.status.issues")} - ${messages(
          s"service.availability.timeline.channel.caption.$channel"
        )}"""
    timelineEvent.message mustBe s"${messages("service.availability.issues.ppn")}"
  }

  def checkGbOrXiEtaContent(etaTimeline: TimeLine, channel: String) = {
    val expectedEtaDateTime = s"$etaTime, ${DateTimeFormatter.formatDateWithoutDayOfWeek(date)}"

    etaTimeline.caption mustBe
      s"""${messages("service.availability.eta")} - ${messages(
          s"service.availability.timeline.channel.caption.$channel"
        )}"""
    etaTimeline.message mustBe (s"${messages("service.availability.eta.estimate")} " +
      s"${messages(s"service.availability.timeline.channel.display.$channel")} " +
      s"${messages("service.availability.eta.availability")} " +
      s"$expectedEtaDateTime. " +
      s"${messages("service.availability.eta.notify.component")}")
  }

  def checkWebOrXmlOrPpnEtaContent(etaTimeline: TimeLine, channel: String) = {
    val expectedEtaDateTime = s"$etaTime, ${DateTimeFormatter.formatDateWithoutDayOfWeek(date)}"

    etaTimeline.caption mustBe
      s"""${messages("service.availability.eta")} - ${messages(
          s"service.availability.timeline.channel.caption.$channel"
        )}"""
    etaTimeline.message mustBe (s"${messages("service.availability.eta.estimate")} " +
      s"${messages(s"service.availability.timeline.channel.display.$channel")} " +
      s"${messages("service.availability.eta.availability")} " +
      s"$expectedEtaDateTime. " +
      s"${messages("service.availability.eta.notify.channel")}")
  }

  def checkBcpWithoutEtaContent(bcp: TimeLine, channel: String) = {

    bcp.caption mustBe
      s"""${messages("service.availability.bcp.invoked")} - ${messages(
          s"service.availability.timeline.channel.caption.$channel"
        )}"""
    bcp.message mustBe (s"${messages("service.availability.bcp.invoked.p1")} ${messages("service.availability.issues.p5")}. " +
      s"${messages("service.availability.bcp.invoked.eta.unknown")}")
    bcp.bcpLink mustBe transitManualLink
  }

  def checkBcpWithEtaContent(bcp: TimeLine, channel: String, dateTimeStr: String) = {

    bcp.caption mustBe
      s"""${messages("service.availability.bcp.invoked")} - ${messages(
          s"service.availability.timeline.channel.caption.$channel"
        )}"""
    bcp.message mustBe (s"${messages("service.availability.bcp.invoked.p1")} ${messages("service.availability.issues.p5")}. " +
      s"""${messages(
          "service.availability.bcp.invoked.eta.known",
          messages(s"service.availability.timeline.channel.caption.$channel"),
          dateTimeStr
        )}""")
    bcp.bcpLink mustBe transitManualLink
  }

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
      document
        .getElementsByTag("caption")
        .first()
        .text() must (fullyMatch regex s"""${messages("service.availability.status.arrivals")}(.+)""")

      document
        .getElementsByTag("caption")
        .get(1)
        .text() must (fullyMatch regex s"""${messages("service.availability.status.departures")}(.+)""")

      document
        .getElementsByTag("caption")
        .get(2)
        .text() must (fullyMatch regex s"""${messages(
          "service.availability.submission.channels.status.heading"
        )}(.+)""")
    }

    "should have a table for arrivals" in {
      document
        .getElementsByClass("govuk-table__header")
        .first()
        .text() mustBe messages("service.availability.system.core.name")

      document
        .getElementsByClass("govuk-table__header")
        .get(1)
        .text() mustBe messages("service.availability.system.availability")

      document
        .getElementsByClass("govuk-table__header")
        .get(2)
        .text() mustBe messages("service.availability.system.known.issues.since")

      document
        .getElementsByClass("govuk-table__cell")
        .first()
        .text() mustBe messages("service.availability.ncts.gb.arrivals")

      document
        .getElementsByClass("govuk-table__cell")
        .get(2)
        .text() mustBe messages("service.availability.status.no.issues")

      document
        .getElementsByClass("govuk-table__cell")
        .get(3)
        .text() mustBe messages("service.availability.ncts.xi.arrivals")

      document
        .getElementsByClass("govuk-table__cell")
        .get(5)
        .text() mustBe messages("service.availability.status.no.issues")
    }

    "should have a table for departures" in {
      document
        .getElementsByClass("govuk-table__header")
        .get(3)
        .text() mustBe messages("service.availability.system.core.name")

      document
        .getElementsByClass("govuk-table__header")
        .get(4)
        .text() mustBe messages("service.availability.system.availability")

      document
        .getElementsByClass("govuk-table__header")
        .get(5)
        .text() mustBe messages("service.availability.system.known.issues.since")
    }

    "should have a table for other systems" in {
      document
        .getElementsByClass("govuk-table__header")
        .get(6)
        .text() mustBe messages("service.availability.channel.name")

      document
        .getElementsByClass("govuk-table__header")
        .get(7)
        .text() mustBe messages("service.availability.system.availability")

      document
        .getElementsByClass("govuk-table__header")
        .get(8)
        .text() mustBe messages("service.availability.system.known.issues.since")
    }

    "should have a last updated time with refresh link for arrivals" in {
      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatTime(now.minusMinutes(31))}. " +
        s"${messages("service.availability.lastUpdated.refresh")} " +
        s"${messages("service.availability.lastUpdated.latest")}"

      document
        .getElementsByClass("govuk-inset-text")
        .first()
        .text() mustBe lastUpdatedText
    }

    "should have a last updated time with refresh link for departures" in {
      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatTime(now.minusSeconds(60))}. " +
        s"${messages("service.availability.lastUpdated.refresh")} " +
        s"${messages("service.availability.lastUpdated.latest")}"

      document
        .getElementsByClass("govuk-inset-text")
        .get(1)
        .text() mustBe lastUpdatedText
    }

    "should have a last updated time with refresh link for other systems" in {

      val lastUpdatedText = s"${messages("service.availability.lastUpdated")} " +
        s"${formatTime(now)}. " +
        s"${messages("service.availability.lastUpdated.refresh")} " +
        s"${messages("service.availability.lastUpdated.latest")}"

      document
        .getElementsByClass("govuk-inset-text")
        .get(2)
        .text() mustBe lastUpdatedText
    }

    "should have an exit survey link" in {
      document
        .body()
        .select("#main-content > div > div > div:nth-child(3) > div > p > a")
        .first()
        .attr("href") mustBe feedbackFrontendUrl
    }

    "should have a get help link" in {
      document
        .body()
        .select(".hmrc-report-technical-issue")
        .first()
        .attr("href") mustBe getHelpUrl
    }

    "should have a related links section" - {

      "with a link to planned downtime" in {
        document.getElementsByTag("h2").first().text() mustBe messages("service.availability.related.links")
        val link =
          document.select(
            "#main-content > div > div > .govuk-grid-row > .govuk-grid-column-full > ul > li:first-child > a"
          )
        link.text() mustBe messages("service.availability.related.links.planned.downtimes")
        link.attr("href") mustBe "/new-computerised-transit-system-service-availability" +
          "/planned-downtime"
      }

      "with a link to downtime history" in {
        val link =
          document.select(
            "#main-content > div > div > .govuk-grid-row > .govuk-grid-column-full > ul > li:nth-child(2) > a"
          )
        link.text() mustBe messages("planned-downtime.related.links.downtime.history")
        link.attr("href") mustBe "/new-computerised-transit-system-service-availability" +
          "/downtime-history"
      }
    }

    "when all services are healthy" - {
      "should show that services are available for arrivals" in {
        document
          .getElementsByClass("govuk-table__cell")
          .get(1)
          .text() mustBe messages("service.availability.status.available")
        document
          .getElementsByClass("govuk-table__cell")
          .get(4)
          .text() mustBe messages("service.availability.status.available")
      }

      "should show that services are available for departures" in {
        document
          .getElementsByClass("govuk-table__cell")
          .get(7)
          .text() mustBe messages("service.availability.status.available")
        document
          .getElementsByClass("govuk-table__cell")
          .get(10)
          .text() mustBe messages("service.availability.status.available")
      }

      "should show that services are available for other systems" in {
        document
          .getElementsByClass("govuk-table__cell")
          .get(13)
          .text() mustBe messages("service.availability.status.available")
        document
          .getElementsByClass("govuk-table__cell")
          .get(16)
          .text() mustBe messages("service.availability.status.available")
      }

      "should not show PPNS row on the other systems" in {
        document.getElementsByClass("govuk-table").last().text() mustNot
          include(messages("service.availability.submission.channels.status.ppn"))
      }

      "should have information which helps with channels with no mention of PPNS" in {
        val summaryBlock = document.getElementsByClass("govuk-details__summary").first()
        val detailsBlock = document.getElementsByClass("govuk-details__text").first()

        summaryBlock
          .getElementsByClass("govuk-details__summary-text")
          .first()
          .text() mustBe messages("service.availability.help.with.channels")

        detailsBlock
          .getElementsByClass("govuk-body")
          .first()
          .text() mustBe messages("service.availability.help.with.channels.p1")

        val para2WithLink = detailsBlock.getElementsByClass("govuk-body")

        para2WithLink.get(1).text() mustBe s"${messages("service.availability.help.with.channels.p2a")}" +
          s" ${messages("service.availability.help.with.channels.p2b")}" +
          s" ${messages("service.availability.help.with.channels.p2c")}"

        para2WithLink.select("a").attr("href") mustBe nctsGuidanceLink

        detailsBlock
          .getElementsByClass("govuk-body")
          .get(2)
          .text() mustBe messages("service.availability.help.with.channels.p3")

        detailsBlock.getElementsByClass("govuk-body").size() mustBe 3
      }

      "should have a paragraph about checking third party software for issues" in {
        val thirdPartyMessage =
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"

        document
          .select("#main-content > div > div > div:nth-child(2) > div > p")
          .first()
          .text() mustBe thirdPartyMessage
      }
    }

    "when all services are unhealthy" - {
      val oldUnhealthyEvent          =
        healthDetailsUnhealthy.copy(statusChangedAt = healthDetailsUnhealthy.statusChangedAt.minusMinutes(1))
      val olderUnhealthyEvent        =
        oldUnhealthyEvent.copy(statusChangedAt = oldUnhealthyEvent.statusChangedAt.minusMinutes(1))
      val statusResponse             = StatusResponse(
        gbDeparturesStatus = oldUnhealthyEvent,
        xiDeparturesStatus = healthDetailsUnhealthy,
        gbArrivalsStatus = healthDetailsUnhealthy,
        xiArrivalsStatus = oldUnhealthyEvent,
        xmlChannelStatus = healthDetailsUnhealthy,
        webChannelStatus = oldUnhealthyEvent,
        ppnStatus = olderUnhealthyEvent,
        createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
      )
      val allUnhealthyView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

      "should show that services have known issues and known issues since time for arrivals" in {
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(1)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(2)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.gbArrivalsStatus.statusChangedAt)
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(4)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(5)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.xiArrivalsStatus.statusChangedAt)

        val gbArrivalEvent = timeLineContent(allUnhealthyView, 0)
        checkGbOrXiIssueContent(gbArrivalEvent, "gb.arrivals")
        gbArrivalEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.gbArrivalsStatus.statusChangedAt)

        val xiArrivalEvent = timeLineContent(allUnhealthyView, 1)
        xiArrivalEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xiArrivalsStatus.statusChangedAt)
        checkGbOrXiIssueContent(xiArrivalEvent, "xi.arrivals")
      }

      "should show that services have known issues and known issues since time for departures" in {
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(7)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(8)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.gbDeparturesStatus.statusChangedAt)
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(10)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(11)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)

        val xiDepartureEvent = timeLineContent(allUnhealthyView, 2)
        xiDepartureEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)
        checkGbOrXiIssueContent(xiDepartureEvent, "xi.departures")

        val gbDepartureEvent = timeLineContent(allUnhealthyView, 3)
        gbDepartureEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.gbDeparturesStatus.statusChangedAt)
        checkGbOrXiIssueContent(gbDepartureEvent, "gb.departures")
      }

      "should show that services have known issues and known issues since time for other systems" in {
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(13)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(14)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.webChannelStatus.statusChangedAt)
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(16)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(17)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.xmlChannelStatus.statusChangedAt)
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(19)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(20)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.ppnStatus.statusChangedAt)

        val xmlEvent = timeLineContent(allUnhealthyView, 4)
        checkWebOrXmlIssueContent(xmlEvent, "xml.channel")
        xmlEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xmlChannelStatus.statusChangedAt)

        val webEvent = timeLineContent(allUnhealthyView, 5)
        checkWebOrXmlIssueContent(webEvent, "web.channel")
        webEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.webChannelStatus.statusChangedAt)

        val ppnEvent = timeLineContent(allUnhealthyView, 6)
        ppnEvent.caption mustBe
          s"""${messages("service.availability.status.issues")} - ${messages(
              s"service.availability.submission.channels.status.ppn"
            )}"""
        ppnEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.ppnStatus.statusChangedAt)
        ppnEvent.message mustBe messages("service.availability.issues.ppn")
      }

      "should have information which helps with channels with mention of PPNS" in {
        val summaryBlock = allUnhealthyView.getElementsByClass("govuk-details__summary").first()
        val detailsBlock = allUnhealthyView.getElementsByClass("govuk-details__text").first()

        summaryBlock
          .getElementsByClass("govuk-details__summary-text")
          .first()
          .text() mustBe messages("service.availability.help.with.channels")

        detailsBlock
          .getElementsByClass("govuk-body")
          .first()
          .text() mustBe messages("service.availability.help.with.channels.p1")

        detailsBlock
          .getElementsByClass("govuk-body")
          .get(1)
          .text() mustBe s"${messages("service.availability.help.with.channels.p2a")}" +
          s" ${messages("service.availability.help.with.channels.p2b")}" +
          s" ${messages("service.availability.help.with.channels.p2c")}"

        detailsBlock
          .getElementsByClass("govuk-body")
          .get(2)
          .text() mustBe messages("service.availability.help.with.channels.p3")

        detailsBlock
          .getElementsByClass("govuk-body")
          .get(3)
          .text() mustBe messages("service.availability.help.with.channels.p4")

        detailsBlock.getElementsByClass("govuk-body").size() mustBe 4
      }

      "should not have a paragraph about checking third party software for issues" in {
        val thirdPartyMessage =
          s"${messages("service.availability.issues.p6")} ${messages("service.availability.issues.xml.channel")}" +
            s" ${messages("service.availability.issues.p7")}"

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
        ppnStatus =
          healthDetailsUnhealthy.copy(statusChangedAt = healthDetailsUnhealthy.statusChangedAt.minusMinutes(10)),
        createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
      )

      val someUnhealthyView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

      "should show that GB arrivals has known issues" in {
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(1)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(4)
          .text() mustBe messages("service.availability.status.available")

        val arrivalsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.arrivals")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(0)
          .text() mustBe arrivalsKnownIssuesParagraph
        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(1)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
      }

      "should show that the GB departures has known issues" in {
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(7)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(10)
          .text() mustBe messages("service.availability.status.available")

        val departuresKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.gb.departures")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(2)
          .text() mustBe departuresKnownIssuesParagraph
        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(3)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
      }

      "should show that the Web channel has known issues" in {
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(13)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(16)
          .text() mustBe messages("service.availability.status.available")

        val webEvent = timeLineContent(someUnhealthyView, 2)
        checkWebOrXmlIssueContent(webEvent, "web.channel")
        webEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.webChannelStatus.statusChangedAt)
      }
    }

    "when XI departures and arrivals and the XML channel are unhealthy but GB departures and arrivals are healthy" - {
      val statusResponse              = StatusResponse(
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
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(1)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(4)
          .text() mustBe messages("service.availability.status.issues")

        val arrivalsKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.xi.arrivals")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(0)
          .text() mustBe arrivalsKnownIssuesParagraph
        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(1)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
      }

      "should show that the XI channel has known issues for departures" in {
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(7)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(10)
          .text() mustBe messages("service.availability.status.issues")

        val departuresKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.ncts.xi.departures")} " +
            s"${messages("service.availability.issues.single.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(2)
          .text() mustBe departuresKnownIssuesParagraph
        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(3)
          .text() mustBe
          s"${messages("service.availability.issues.p4")} ${messages("service.availability.issues.p5")}."
      }

      "should show that the XML channel has known issues" in {
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(13)
          .text() mustBe messages("service.availability.status.available")
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(16)
          .text() mustBe messages("service.availability.status.issues")
        someUnhealthyView
          .getElementsByClass("govuk-table__cell")
          .get(17)
          .text() mustBe DateTimeFormatter.formatDateTime(statusResponse.xmlChannelStatus.statusChangedAt)

        val xmlChannelKnownIssuesParagraph =
          s"${messages("service.availability.issues.p1")} " +
            s"${messages("service.availability.submission.channels.status.xml")} " +
            s"${messages("service.availability.issues.channel")} " +
            s"${messages("service.availability.issues.webXML.channel")} " +
            s"${messages("service.availability.issues.p3")}"

        someUnhealthyView
          .getElementsByClass("govuk-body")
          .get(9)
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
        webChannelStatus = healthDetailsUnhealthy,
        ppnStatus = healthDetailsUnhealthy,
        createdTs = LocalDateTime.of(2022, 1, 24, 0, 0, 0)
      )

      val ppnUnhealthyView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

      "should show PPNS channel has known issues" in {
        val ppnEvent = timeLineContent(ppnUnhealthyView, 2)
        ppnEvent.caption mustBe s"""${messages("service.availability.status.issues")} - ${messages(
            "service.availability.submission.channels.status.ppn"
          )}"""
        ppnEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.ppnStatus.statusChangedAt)
        ppnEvent.message mustBe messages("service.availability.issues.ppn")
      }
    }

    "when there is PlannedDowntime" - {
      "should show planned downtime for the availability status" in {

        val statusResponse = StatusResponse(
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
          Some(PlannedDowntime(date, time.minusMinutes(1), date, time.plusMinutes(1), GBArrivals)),
          Some(PlannedDowntime(date, time.minusMinutes(1), date, time.plusMinutes(1), XIArrivals)),
          Some(PlannedDowntime(date, time.minusMinutes(1), date, time.plusMinutes(1), GBDepartures)),
          Some(PlannedDowntime(date, time.minusMinutes(1), date, time.plusMinutes(1), XIDepartures))
        )

        val allHealthyWithPlannedDowntime: Document = Jsoup.parse(view(statusResponse, plannedDowntimeViewModel).body)

        allHealthyWithPlannedDowntime
          .getElementsByClass("govuk-table__cell")
          .get(1)
          .text() mustBe messages("service.availability.status.planned.downtime")
        allHealthyWithPlannedDowntime
          .getElementsByClass("govuk-table__cell")
          .get(2)
          .text() mustBe messages("service.availability.status.no.issues")
        allHealthyWithPlannedDowntime
          .getElementsByClass("govuk-table__cell")
          .get(4)
          .text() mustBe messages("service.availability.status.planned.downtime")
        allHealthyWithPlannedDowntime
          .getElementsByClass("govuk-table__cell")
          .get(5)
          .text() mustBe messages("service.availability.status.no.issues")
        allHealthyWithPlannedDowntime
          .getElementsByClass("govuk-table__cell")
          .get(7)
          .text() mustBe messages("service.availability.status.planned.downtime")
        allHealthyWithPlannedDowntime
          .getElementsByClass("govuk-table__cell")
          .get(8)
          .text() mustBe messages("service.availability.status.no.issues")
        allHealthyWithPlannedDowntime
          .getElementsByClass("govuk-table__cell")
          .get(10)
          .text() mustBe messages("service.availability.status.planned.downtime")
        allHealthyWithPlannedDowntime
          .getElementsByClass("govuk-table__cell")
          .get(11)
          .text() mustBe messages("service.availability.status.no.issues")
      }
    }

    "should not show ETA in timeline if the channel is healthy" in {
      val allChannelsHealthyButHasEtas = allChannelsHealthyWithoutEtas.copy(
        timelineEntries = Seq(GBDepartures, GBArrivals, XIDepartures, XIArrivals, Web, XML, PPN).map(eta(_, now))
      )
      val healthyView: Document        = Jsoup.parse(view(allChannelsHealthyButHasEtas, PlannedDowntimeViewModel.default).body)
      val timelineEvents               = healthyView.getElementsByClass("hmrc-timeline__event")
      timelineEvents mustBe empty
    }

    "should show Known issues and corresponding ETAs(if present) in timeline from most recent to oldest events" - {

      "when GB and XI Departures are unhealthy and GB has an ETA" in {
        val statusResponse                    = allChannelsHealthyWithoutEtas.copy(
          gbDeparturesStatus = healthDetailsUnhealthy.copy(statusChangedAt = tenMinutesAgo),
          xiDeparturesStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
          timelineEntries = Seq(eta(GBDepartures, twoMinutesAgo))
        )
        val unhealthyDeparturesView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

        val gbEtaEvent = timeLineContent(unhealthyDeparturesView, 0)
        gbEtaEvent.time mustBe DateTimeFormatter.formatDateTime(twoMinutesAgo)
        checkGbOrXiEtaContent(gbEtaEvent, "gb.departures")

        val xiDepartureEvent = timeLineContent(unhealthyDeparturesView, 1)
        xiDepartureEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)
        checkGbOrXiIssueContent(xiDepartureEvent, "xi.departures")

        val gbDeparturesEvent = timeLineContent(unhealthyDeparturesView, 2)
        checkGbOrXiIssueContent(gbDeparturesEvent, "gb.departures")
        gbDeparturesEvent.time mustBe DateTimeFormatter.formatDateTime(
          statusResponse.gbDeparturesStatus.statusChangedAt
        )
      }

      "when GB and XI Arrivals are unhealthy and XI has an ETA" in {
        val statusResponse                  = allChannelsHealthyWithoutEtas.copy(
          gbArrivalsStatus = healthDetailsUnhealthy.copy(statusChangedAt = tenMinutesAgo),
          xiArrivalsStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
          timelineEntries = Seq(eta(XIArrivals, twoMinutesAgo))
        )
        val unhealthyArrivalsView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

        val xiEtaEvent = timeLineContent(unhealthyArrivalsView, 0)
        xiEtaEvent.time mustBe DateTimeFormatter.formatDateTime(twoMinutesAgo)
        checkGbOrXiEtaContent(xiEtaEvent, "xi.arrivals")

        val xiArrivalsEvent = timeLineContent(unhealthyArrivalsView, 1)
        xiArrivalsEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xiArrivalsStatus.statusChangedAt)
        checkGbOrXiIssueContent(xiArrivalsEvent, "xi.arrivals")

        val gbArrivalsEvent = timeLineContent(unhealthyArrivalsView, 2)
        checkGbOrXiIssueContent(gbArrivalsEvent, "gb.arrivals")
        gbArrivalsEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.gbArrivalsStatus.statusChangedAt)
      }

      "when Web, XML and PPN are unhealthy and have an ETA" in {
        val now                               = LocalDateTime.now
        val statusResponse                    = allChannelsHealthyWithoutEtas.copy(
          webChannelStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
          xmlChannelStatus = healthDetailsUnhealthy.copy(statusChangedAt = tenMinutesAgo),
          ppnStatus = healthDetailsUnhealthy.copy(statusChangedAt = twentyMinutesAgo),
          timelineEntries = Seq(eta(XML, twoMinutesAgo), eta(Web, fewSecondsAgo), eta(PPN, now))
        )
        val unhealthyDeparturesView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

        val ppnEtaEvent = timeLineContent(unhealthyDeparturesView, 0)
        ppnEtaEvent.time mustBe DateTimeFormatter.formatDateTime(now)
        checkWebOrXmlOrPpnEtaContent(ppnEtaEvent, "ppn")

        val webEtaEvent = timeLineContent(unhealthyDeparturesView, 1)
        webEtaEvent.time mustBe DateTimeFormatter.formatDateTime(fewSecondsAgo)
        checkWebOrXmlOrPpnEtaContent(webEtaEvent, "web.channel")

        val xmlEtaEvent = timeLineContent(unhealthyDeparturesView, 2)
        xmlEtaEvent.time mustBe DateTimeFormatter.formatDateTime(twoMinutesAgo)
        checkWebOrXmlOrPpnEtaContent(xmlEtaEvent, "xml.channel")

        val webIssuesEvent = timeLineContent(unhealthyDeparturesView, 3)
        webIssuesEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.webChannelStatus.statusChangedAt)
        checkWebOrXmlIssueContent(webIssuesEvent, "web.channel")

        val xmlIssuesEvent = timeLineContent(unhealthyDeparturesView, 4)
        xmlIssuesEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xmlChannelStatus.statusChangedAt)
        checkWebOrXmlIssueContent(xmlIssuesEvent, "xml.channel")

        val ppnIssuesEvent = timeLineContent(unhealthyDeparturesView, 5)
        ppnIssuesEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.ppnStatus.statusChangedAt)
        checkPpnIssueContent(ppnIssuesEvent, "ppn")
      }

    }

    "should show BCP details in timeline from most recent to oldest events" - {

      "when GB and XI Departures are unhealthy and GB has BCP invoked without an ETA" in {
        val statusResponse                    = allChannelsHealthyWithoutEtas.copy(
          gbDeparturesStatus = healthDetailsUnhealthy.copy(statusChangedAt = tenMinutesAgo),
          xiDeparturesStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
          timelineEntries = Seq(bcpWithoutEta(GBDepartures, twoMinutesAgo))
        )
        val unhealthyDeparturesView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

        val gbDeparturesBcpWithoutEta = timeLineContent(unhealthyDeparturesView, 0)
        gbDeparturesBcpWithoutEta.time mustBe DateTimeFormatter.formatDateTime(twoMinutesAgo)
        checkBcpWithoutEtaContent(gbDeparturesBcpWithoutEta, "gb.departures")

        val xiDepartureEvent = timeLineContent(unhealthyDeparturesView, 1)
        xiDepartureEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)
        checkGbOrXiIssueContent(xiDepartureEvent, "xi.departures")

        val gbDeparturesEvent = timeLineContent(unhealthyDeparturesView, 2)
        checkGbOrXiIssueContent(gbDeparturesEvent, "gb.departures")
        gbDeparturesEvent.time mustBe DateTimeFormatter.formatDateTime(
          statusResponse.gbDeparturesStatus.statusChangedAt
        )
      }

      "when GB and XI Arrivals are unhealthy and XI has BCP invoked without an ETA" in {
        val statusResponse                  = allChannelsHealthyWithoutEtas.copy(
          gbArrivalsStatus = healthDetailsUnhealthy.copy(statusChangedAt = fewSecondsAgo),
          xiArrivalsStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
          timelineEntries = Seq(bcpWithoutEta(XIArrivals, twoMinutesAgo))
        )
        val unhealthyArrivalsView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

        val gbArrivalsEvent = timeLineContent(unhealthyArrivalsView, 0)
        checkGbOrXiIssueContent(gbArrivalsEvent, "gb.arrivals")
        gbArrivalsEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.gbArrivalsStatus.statusChangedAt)

        val xiArrivalsBcpWithoutEta = timeLineContent(unhealthyArrivalsView, 1)
        xiArrivalsBcpWithoutEta.time mustBe DateTimeFormatter.formatDateTime(twoMinutesAgo)
        checkBcpWithoutEtaContent(xiArrivalsBcpWithoutEta, "xi.arrivals")

        val xiArrivalsEvent = timeLineContent(unhealthyArrivalsView, 2)
        xiArrivalsEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xiArrivalsStatus.statusChangedAt)
        checkGbOrXiIssueContent(xiArrivalsEvent, "xi.arrivals")
      }

      "when GB and XI Departures are unhealthy and GB has BCP invoked with an ETA" in {
        val statusResponse                    = allChannelsHealthyWithoutEtas.copy(
          gbDeparturesStatus = healthDetailsUnhealthy.copy(statusChangedAt = tenMinutesAgo),
          xiDeparturesStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
          timelineEntries = Seq(bcpWithEta(GBDepartures, twoMinutesAgo))
        )
        val unhealthyDeparturesView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

        val gbDeparturesBcpWithEta  = timeLineContent(unhealthyDeparturesView, 0)
        gbDeparturesBcpWithEta.time mustBe DateTimeFormatter.formatDateTime(twoMinutesAgo)
        val gbDeparturesEtaDateTime = s"$etaTime, ${DateTimeFormatter.formatDateWithoutDayOfWeek(etaDate)}"
        checkBcpWithEtaContent(gbDeparturesBcpWithEta, "gb.departures", gbDeparturesEtaDateTime)

        val xiDepartureEvent = timeLineContent(unhealthyDeparturesView, 1)
        xiDepartureEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xiDeparturesStatus.statusChangedAt)
        checkGbOrXiIssueContent(xiDepartureEvent, "xi.departures")

        val gbDeparturesEvent = timeLineContent(unhealthyDeparturesView, 2)
        checkGbOrXiIssueContent(gbDeparturesEvent, "gb.departures")
        gbDeparturesEvent.time mustBe DateTimeFormatter.formatDateTime(
          statusResponse.gbDeparturesStatus.statusChangedAt
        )
      }

      "when GB and XI Arrivals are unhealthy and XI has BCP invoked with an ETA" in {
        val statusResponse                  = allChannelsHealthyWithoutEtas.copy(
          gbArrivalsStatus = healthDetailsUnhealthy.copy(statusChangedAt = fewSecondsAgo),
          xiArrivalsStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
          timelineEntries = Seq(bcpWithEta(XIArrivals, twoMinutesAgo))
        )
        val unhealthyArrivalsView: Document = Jsoup.parse(view(statusResponse, PlannedDowntimeViewModel.default).body)

        val gbArrivalsEvent = timeLineContent(unhealthyArrivalsView, 0)
        checkGbOrXiIssueContent(gbArrivalsEvent, "gb.arrivals")
        gbArrivalsEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.gbArrivalsStatus.statusChangedAt)

        val xiArrivalsBcpWithEta  = timeLineContent(unhealthyArrivalsView, 1)
        xiArrivalsBcpWithEta.time mustBe DateTimeFormatter.formatDateTime(twoMinutesAgo)
        val xiArrivalsEtaDateTime = s"$etaTime, ${DateTimeFormatter.formatDateWithoutDayOfWeek(etaDate)}"
        checkBcpWithEtaContent(xiArrivalsBcpWithEta, "xi.arrivals", xiArrivalsEtaDateTime)

        val xiArrivalsEvent = timeLineContent(unhealthyArrivalsView, 2)
        xiArrivalsEvent.time mustBe DateTimeFormatter.formatDateTime(statusResponse.xiArrivalsStatus.statusChangedAt)
        checkGbOrXiIssueContent(xiArrivalsEvent, "xi.arrivals")
      }
    }
  }

  val document: Document = {

    val lastMessageAccepted = now

    val healthDetails = healthDetailsHealthy.copy(lastMessageAccepted = Some(lastMessageAccepted))

    Jsoup.parse(
      view(
        StatusResponse(
          gbDeparturesStatus = healthDetails,
          xiDeparturesStatus = healthDetails,
          gbArrivalsStatus = healthDetails,
          xiArrivalsStatus = healthDetails,
          xmlChannelStatus = healthDetails,
          webChannelStatus = healthDetails,
          ppnStatus = healthDetails,
          createdTs = now
        ),
        PlannedDowntimeViewModel.default
      ).body
    )
  }
}
