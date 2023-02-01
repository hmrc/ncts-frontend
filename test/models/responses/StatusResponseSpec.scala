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

package models.responses

import base.SpecBase
import models.responses.ErrorResponse.StatusResponseError
import models.responses.StatusResponse.{StatusResponseReads, dateTimeOrdering}
import models._
import org.scalatest.enablers.Sortable
import org.scalatest.matchers.must.Matchers
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

import java.time.{LocalDate, LocalDateTime}

class StatusResponseSpec extends SpecBase with Matchers {

  private val statusChangedAt =
    LocalDateTime.of(2022, 1, 1, 10, 25, 55)

  private val lastMessageAccepted =
    LocalDateTime.of(2022, 1, 1, 10, 25, 55)

  private val etaDate                = LocalDate.of(2022, 3, 23)
  private val etaTime                = "10am BST"
  private val healthDetailsHealthy   =
    HealthDetails(healthy = true, statusChangedAt = statusChangedAt, lastMessageAccepted = Some(lastMessageAccepted))
  private val healthDetailsUnhealthy =
    HealthDetails(healthy = false, statusChangedAt = statusChangedAt, lastMessageAccepted = Some(lastMessageAccepted))
  lazy val allHealthyResp            = StatusResponse(
    healthDetailsHealthy,
    healthDetailsHealthy,
    healthDetailsHealthy,
    healthDetailsHealthy,
    healthDetailsHealthy,
    healthDetailsHealthy,
    healthDetailsHealthy,
    Nil,
    LocalDateTime.now
  )
  private val twentyMinutesAgo       = LocalDateTime.now.minusMinutes(20)
  private val tenMinutesAgo          = LocalDateTime.now.minusMinutes(10)
  private val fiveMinutesAgo         = LocalDateTime.now.minusMinutes(5)
  private val twoMinutesAgo          = LocalDateTime.now.minusMinutes(2)
  private val fewSecondsAgo          = LocalDateTime.now.minusSeconds(15)

  implicit val dateTimeReverseSortable: Sortable[Seq[LocalDateTime]] =
    Sortable.sortableNatureOfSeq(dateTimeOrdering.reverse)

  private def eta(ch: Channel, createdTs: LocalDateTime) =
    TimelineUpdate(ch, Option("10am GMT"), Option(LocalDate.now()), businessContinuityFlag = false, createdTs)

  "StatusResponseReads" - {
    "should return a StatusResponse when status is OK and can be parsed for GB/XI departures true, GB/XI arrivals false and ETA for XI arrivals and XML" in {

      val depHealthyArrUnhealthyJson = json(true, false, false, timelineEntriesJson(etaDate, etaTime))
      val createdTimestamp           = LocalDateTime.of(2022, 1, 1, 10, 25, 55)
      val etas                       = Seq(
        TimelineUpdate(XIArrivals, Option(etaTime), Option(etaDate), businessContinuityFlag = false, createdTimestamp),
        TimelineUpdate(XML, Option(etaTime), Option(etaDate), businessContinuityFlag = false, createdTimestamp)
      )
      val expectedResult             =
        Right(
          StatusResponse(
            gbDeparturesStatus = healthDetailsHealthy,
            xiDeparturesStatus = healthDetailsHealthy,
            gbArrivalsStatus = healthDetailsUnhealthy,
            xiArrivalsStatus = healthDetailsUnhealthy,
            xmlChannelStatus = healthDetailsUnhealthy,
            webChannelStatus = healthDetailsUnhealthy,
            ppnStatus = healthDetailsUnhealthy,
            timelineEntries = etas,
            createdTs = createdTimestamp
          )
        )

      val httpResponse = HttpResponse(Status.OK, depHealthyArrUnhealthyJson)

      val result = StatusResponseReads.read("GET", "url", httpResponse)

      result mustBe expectedResult
    }

    "should return a StatusResponse when status is OK and can be parsed for GB/XI departures false and GB/XI arrivals true" in {

      val depUnhealthyArrHealthy = json(false, true, true)

      val expectedResult = Right(
        StatusResponse(
          gbDeparturesStatus = healthDetailsUnhealthy,
          xiDeparturesStatus = healthDetailsUnhealthy,
          gbArrivalsStatus = healthDetailsHealthy,
          xiArrivalsStatus = healthDetailsHealthy,
          xmlChannelStatus = healthDetailsHealthy,
          webChannelStatus = healthDetailsHealthy,
          ppnStatus = healthDetailsHealthy,
          createdTs = LocalDateTime.of(2022, 1, 1, 10, 25, 55)
        )
      )

      val httpResponse = HttpResponse(Status.OK, depUnhealthyArrHealthy)

      val result = StatusResponseReads.read("GET", "url", httpResponse)

      result mustBe expectedResult
    }

    "should return StatusResponseError" - {
      "the response is on an invalid format" in {
        val invalidJson =
          """
            |{
            |  "bad": "json"
            |}
              """.stripMargin

        val httpResponse = HttpResponse(Status.OK, invalidJson)

        val result = StatusResponseReads.read("GET", "url", httpResponse)

        result mustBe Symbol("left")
      }

      "the response has an unexpected error" in {
        val expectedResult =
          Left(StatusResponseError("Unexpected error occurred when checking service status: something went wrong"))

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "something went wrong")

        val result = StatusResponseReads.read("GET", "url", httpResponse)

        result mustBe expectedResult
      }
    }
  }

  "xmlAndWebHealthy" - {

    lazy val resp = StatusResponse(
      healthDetailsHealthy,
      healthDetailsHealthy,
      healthDetailsHealthy,
      healthDetailsHealthy,
      healthDetailsHealthy,
      healthDetailsHealthy,
      healthDetailsHealthy,
      Nil,
      LocalDateTime.now
    )

    "should return true if both xml and web are healthy in the response" in {
      resp.xmlAndWebHealthy mustBe true
    }

    "should return false if xml is healthy but web is unhealthy in the response" in {
      resp.copy(xmlChannelStatus = healthDetailsUnhealthy).xmlAndWebHealthy mustBe false
    }

    "should return false if web is healthy but xml is unhealthy in the response" in {
      resp.copy(webChannelStatus = healthDetailsUnhealthy).xmlAndWebHealthy mustBe false
    }
  }

  "arrivalsWithKnownIssuesAndEta should return ETA and Known issues sorted - recent to old" in {
    val resp     = allHealthyResp.copy(
      gbArrivalsStatus = healthDetailsUnhealthy.copy(statusChangedAt = twoMinutesAgo),
      xiArrivalsStatus = healthDetailsUnhealthy.copy(statusChangedAt = tenMinutesAgo),
      timelineEntries = Seq(eta(XIArrivals, fiveMinutesAgo), eta(GBArrivals, LocalDateTime.now))
    )
    val timeline = resp.arrivalsWithKnownIssuesAndEta
    timeline.size mustBe 4
    timeline.map(_.issueSince) mustBe sorted
    timeline.head.channel mustBe GBArrivals
    timeline.head.eta mustBe defined
    timeline(1).channel mustBe GBArrivals
    timeline(1).eta mustBe empty
    timeline(2).channel mustBe XIArrivals
    timeline(2).eta mustBe defined
    timeline(3).channel mustBe XIArrivals
    timeline(3).eta mustBe empty
  }

  "departureWithKnownIssuesAndEta should return ETA and Known issues sorted - recent to old" in {
    val resp     = allHealthyResp.copy(
      gbDeparturesStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
      xiDeparturesStatus = healthDetailsUnhealthy.copy(statusChangedAt = tenMinutesAgo),
      timelineEntries = Seq(eta(GBDepartures, twoMinutesAgo), eta(XIDepartures, LocalDateTime.now))
    )
    val timeline = resp.departuresWithKnownIssuesAndEta
    timeline.size mustBe 4
    timeline.map(_.issueSince) mustBe sorted
    timeline.head.channel mustBe XIDepartures
    timeline.head.eta mustBe defined
    timeline(1).channel mustBe GBDepartures
    timeline(1).eta mustBe defined
    timeline(2).channel mustBe GBDepartures
    timeline(2).eta mustBe empty
    timeline(3).channel mustBe XIDepartures
    timeline(3).eta mustBe empty
  }

  "channelsWithKnownIssuesAndEta should return ETA and Known issues sorted - recent to old" in {
    val resp     = allHealthyResp.copy(
      webChannelStatus = healthDetailsUnhealthy.copy(statusChangedAt = fiveMinutesAgo),
      xmlChannelStatus = healthDetailsUnhealthy.copy(statusChangedAt = tenMinutesAgo),
      ppnStatus = healthDetailsUnhealthy.copy(statusChangedAt = twentyMinutesAgo),
      timelineEntries = Seq(eta(Web, twoMinutesAgo), eta(XML, fewSecondsAgo), eta(PPN, LocalDateTime.now))
    )
    val timeline = resp.channelsWithKnownIssuesAndEta
    timeline.size mustBe 6
    timeline.map(_.issueSince) mustBe sorted
    timeline.head.channel mustBe PPN
    timeline.head.eta mustBe defined
    timeline(1).channel mustBe XML
    timeline(1).eta mustBe defined
    timeline(2).channel mustBe Web
    timeline(2).eta mustBe defined
    timeline(3).channel mustBe Web
    timeline(3).eta mustBe empty
    timeline(4).channel mustBe XML
    timeline(4).eta mustBe empty
    timeline(5).channel mustBe PPN
    timeline(5).eta mustBe empty
  }

  "TimelineUpdate" - {

    "should serialise and de-serialise correctly" in {

      val obj = TimelineUpdate(
        XML,
        Some("fake time"),
        Some(LocalDate.now()),
        businessContinuityFlag = true,
        LocalDateTime.now()
      )

      val json = Json.toJson(obj)

      json.as[TimelineUpdate] mustBe obj
    }
  }

  def json(
    departuresHealthy: Boolean,
    arrivalsHealthy: Boolean,
    otherChannelsHealthy: Boolean,
    timelineEntriesJson: String = "[]"
  ): String =
    s"""
       |{
       |  "gbDeparturesStatus": {
       |    "healthy": $departuresHealthy,
       |    "statusChangedAt": "$statusChangedAt",
       |    "lastMessageAccepted": "$lastMessageAccepted"
       |  },
       |  "xiDeparturesStatus": {
       |    "healthy": $departuresHealthy,
       |    "statusChangedAt": "$statusChangedAt",
       |    "lastMessageAccepted": "$lastMessageAccepted"
       |  },
       |  "gbArrivalsStatus": {
       |    "healthy": $arrivalsHealthy,
       |    "statusChangedAt": "$statusChangedAt",
       |    "lastMessageAccepted": "$lastMessageAccepted"
       |  },
       |  "xiArrivalsStatus": {
       |    "healthy": $arrivalsHealthy,
       |    "statusChangedAt": "$statusChangedAt",
       |    "lastMessageAccepted": "$lastMessageAccepted"
       |  },
       |  "xmlChannelStatus": {
       |    "healthy": $otherChannelsHealthy,
       |    "statusChangedAt": "$statusChangedAt",
       |    "lastMessageAccepted": "$lastMessageAccepted"
       |  },
       |  "webChannelStatus": {
       |    "healthy": $otherChannelsHealthy,
       |    "statusChangedAt": "$statusChangedAt",
       |    "lastMessageAccepted": "$lastMessageAccepted"
       |  },
       |  "ppnStatus": {
       |    "healthy": $otherChannelsHealthy,
       |    "statusChangedAt": "$statusChangedAt",
       |    "lastMessageAccepted": "$lastMessageAccepted"
       |  },
       |  "timelineEntries": $timelineEntriesJson,
       |  "createdTs": "2022-01-01T10:25:55"
       |}""".stripMargin

  def timelineEntriesJson(date: LocalDate, time: String): String =
    s"""
       |[
       |  {
       |    "channel": "XI Arrivals",
       |    "time": "$time",
       |    "date": "$date",
       |    "businessContinuityFlag": false,
       |    "createdTs": "2022-01-01T10:25:55"
       |  },
       |  {
       |    "channel": "XML channel",
       |    "time": "$time",
       |    "date": "$date",
       |    "businessContinuityFlag": false,
       |    "createdTs": "2022-01-01T10:25:55"
       |  }
       |]""".stripMargin

}
