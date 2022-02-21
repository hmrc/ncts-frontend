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

package models.responses

import models.Channel
import models.responses.DowntimeResponse.DowntimeResponseReads
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDateTime

class DowntimeResponseSpec extends AnyWordSpec with Matchers {

  private val start =
    LocalDateTime.of(2022, 1, 1, 10, 25, 55)

  private val end =
    LocalDateTime.of(2022, 1, 1, 10, 25, 55)


  "DowntimeResponseReads" should {
    "return a DowntimeResponse when status is OK and can be parsed" in {

      //val depHealthyArrUnhealthyJson = json(true, false, false)

      val expectedResult = DowntimeResponse(Seq(Downtime(Channel.gbDepartures, start, end)))

      println(Json.toJson(expectedResult))
      val httpResponse = HttpResponse(Status.OK, "depHealthyArrUnhealthyJson")

      val Right(result) = DowntimeResponseReads.read("GET", "url", httpResponse)

      result mustBe expectedResult
    }

/*    "return a DowntimeResponse when status is OK and can be parsed for GB/XI departures false and GB/XI arrivals true" in {

      val depUnhealthyArrHealthy = json(false, true, true)

      val expectedResult = DowntimeResponse(Seq(Downtime(Channel.gbDepartures, start, end)))

      val httpResponse = HttpResponse(Status.OK, depUnhealthyArrHealthy)

      val Right(result) = DowntimeResponseReads.read("GET", "url", httpResponse)

      result mustBe expectedResult
    }

    "return DowntimeResponseError" when {
      "the response is on an invalid format" in {
        val invalidJson =
          """
            |{
            |  "bad": "json"
            |}
              """.stripMargin

        val httpResponse = HttpResponse(Status.OK, invalidJson)

        val result = DowntimeResponseReads.read("GET", "url", httpResponse)

        result mustBe 'left
      }

      "the response has an unexpected error" in {
        val expectedResult = DowntimeResponseError("Unexpected error occurred when checking service status: something went wrong")

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "something went wrong")

        val Left(result) = DowntimeResponseReads.read("GET", "url", httpResponse)

        result mustBe expectedResult
      }
    }*/
  }

/*
  def json(departuresHealthy: Boolean, arrivalsHealthy: Boolean, otherChannelsHealthy: Boolean): String = {
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
       |  "createdTs": "2022-01-01T10:25:55"
       |}""".stripMargin
  }
*/
}
