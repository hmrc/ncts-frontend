/*
 * Copyright 2021 HM Revenue & Customs
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

class StatusResponseSpec extends AnyWordSpec with Matchers {

  "StatusResponseReads" should {
    "return a StatusResponse when status is OK and can be parsed" in {
      val json =
        """
          |{
          |  "departuresApiHealthy": true
          |}
          """.stripMargin

      val expectedResult = StatusResponse(departuresApiHealthy = true)

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

        val expectedResult = StatusResponseError("Response in an unexpected format: error.path.missing")

        val httpResponse = HttpResponse(Status.OK, invalidJson)

        val Left(result) = StatusResponseReads.read("GET", "url", httpResponse)

        result mustBe expectedResult
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
