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

import models.responses.ErrorResponse.StatusResponseError
import models.responses.StatusResponse.StatusResponseReads
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import uk.gov.hmrc.http.HttpResponse
import java.time.LocalDateTime

class StatusResponseSpec extends AnyWordSpec with Matchers {

  private val statusChangedAt =
    LocalDateTime.of(2022, 1, 1, 10, 25, 55)
  private val healthDetailsHealthy =
    HealthDetails(healthy = true, statusChangedAt = statusChangedAt)
  private val healthDetailsUnhealthy =
    HealthDetails(healthy = false, statusChangedAt = statusChangedAt)

  "StatusResponseReads" should {
    "return a StatusResponse when status is OK and can be parsed for GB/XI departures true and GB/XI arrivals false" in {
      val json =
        s"""
           |{
           |  "gbDeparturesStatus": {
           |    "healthy": true,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "xiDeparturesStatus": {
           |    "healthy": true,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "gbArrivalsStatus": {
           |    "healthy": false,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "xiArrivalsStatus": {
           |    "healthy": false,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "xmlChannelStatus": {
           |    "healthy": false,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "webChannelStatus": {
           |    "healthy": false,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "createdTs": "2022-01-01T10:25:55"
           |}
          """.stripMargin

      val expectedResult = StatusResponse(
        gbDeparturesStatus = healthDetailsHealthy,
        xiDeparturesStatus = healthDetailsHealthy,
        gbArrivalsStatus = healthDetailsUnhealthy,
        xiArrivalsStatus = healthDetailsUnhealthy,
        xmlChannelStatus = healthDetailsUnhealthy,
        webChannelStatus = healthDetailsUnhealthy,
        createdTs = LocalDateTime.of(2022, 1, 1, 10, 25, 55)
      )

      val httpResponse = HttpResponse(Status.OK, json)

      val Right(result) = StatusResponseReads.read("GET", "url", httpResponse)

      result mustBe expectedResult
    }

    "return a StatusResponse when status is OK and can be parsed for GB/XI departures false and GB/XI arrivals true" in {
      val json =
        s"""
           |{
           |  "gbDeparturesStatus": {
           |    "healthy": false,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "xiDeparturesStatus": {
           |    "healthy": false,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "gbArrivalsStatus": {
           |    "healthy": true,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "xiArrivalsStatus": {
           |    "healthy": true,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "xmlChannelStatus": {
           |    "healthy": true,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "webChannelStatus": {
           |    "healthy": true,
           |    "statusChangedAt": "${statusChangedAt}"
           |  },
           |  "createdTs": "2022-01-01T10:25:55"
           |}
          """.stripMargin

      val expectedResult = StatusResponse(
        gbDeparturesStatus = healthDetailsUnhealthy,
        xiDeparturesStatus = healthDetailsUnhealthy,
        gbArrivalsStatus = healthDetailsHealthy,
        xiArrivalsStatus = healthDetailsHealthy,
        xmlChannelStatus = healthDetailsHealthy,
        webChannelStatus = healthDetailsHealthy,
        createdTs = LocalDateTime.of(2022, 1, 1, 10, 25, 55)
      )

      val httpResponse = HttpResponse(Status.OK, json)

      val Right(result) = StatusResponseReads.read("GET", "url", httpResponse)

      result mustBe expectedResult
    }

    "return StatusResponseError" when {
      "the response is on an invalid format" in {
        val invalidJson =
          """
            |{
            |  "bad": "json"
            |}
              """.stripMargin

        val httpResponse = HttpResponse(Status.OK, invalidJson)

        val result = StatusResponseReads.read("GET", "url", httpResponse)

        result mustBe 'left
      }

      "the response has an unexpected error" in {
        val expectedResult = StatusResponseError("Unexpected error occurred when checking service status: something went wrong")

        val httpResponse = HttpResponse(Status.INTERNAL_SERVER_ERROR, "something went wrong")

        val Left(result) = StatusResponseReads.read("GET", "url", httpResponse)

        result mustBe expectedResult
      }
    }
  }

  "xmlAndWebHealthy" should {

    lazy val resp = StatusResponse(healthDetailsHealthy, healthDetailsHealthy, healthDetailsHealthy, healthDetailsHealthy,
      healthDetailsHealthy,healthDetailsHealthy, LocalDateTime.now)

    "return true if both xml and web are healthy in the response" in {
      resp.xmlAndWebHealthy mustBe true
    }

    "return false if xml is healthy but web is unhealthy in the response" in {
      resp.copy(xmlChannelStatus = healthDetailsUnhealthy).xmlAndWebHealthy mustBe false
    }

    "return false if web is healthy but xml is unhealthy in the response" in {
      resp.copy(webChannelStatus = healthDetailsUnhealthy).xmlAndWebHealthy mustBe false
    }
  }

}
