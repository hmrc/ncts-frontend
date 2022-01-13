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

  "StatusResponseReads" should {
    "return a StatusResponse when status is OK and can be parsed for GB/XI departures true and GB/XI arrivals false" in {
      val json =
        """
          |{
          |  "gbDeparturesHealthy": true,
          |  "xiDeparturesHealthy": true,
          |  "gbArrivalsHealthy": false,
          |  "xiArrivalsHealthy": false,
          |  "createdTs":"2022-01-01T10:25:55"
          |}
          """.stripMargin

      val expectedResult = StatusResponse(
        gbDeparturesHealthy = true,
        xiDeparturesHealthy = true,
        gbArrivalsHealthy = false,
        xiArrivalsHealthy = false,
        createdTs = LocalDateTime.of(2022, 1, 1, 10, 25, 55)
      )

      val httpResponse = HttpResponse(Status.OK, json)

      val Right(result) = StatusResponseReads.read("GET", "url", httpResponse)

      result mustBe expectedResult
    }

    "return a StatusResponse when status is OK and can be parsed for GB/XI departures false and GB/XI arrivals true" in {
      val json =
        """
          |{
          |  "gbDeparturesHealthy": false,
          |  "xiDeparturesHealthy": false,
          |  "gbArrivalsHealthy": true,
          |  "xiArrivalsHealthy": true,
          |  "createdTs":"2022-01-01T10:25:55"
          |}
          """.stripMargin

      val expectedResult = StatusResponse(
        gbDeparturesHealthy = false,
        xiDeparturesHealthy = false,
        gbArrivalsHealthy = true,
        xiArrivalsHealthy = true,
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
}
