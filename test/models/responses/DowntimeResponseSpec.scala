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

import models._
import models.responses.DowntimeResponse.DowntimeResponseReads
import models.responses.ErrorResponse.DowntimeResponseError
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.libs.json.{JsObject, JsString, Json}
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDateTime

class DowntimeResponseSpec extends AnyWordSpec with Matchers {

  val dateStart = LocalDateTime.of(2018, 2, 1, 0, 0)
  val dateEnd = LocalDateTime.of(2018, 2, 1, 5, 33, 20)

  val jsonStartFromMongo: JsObject = Json.obj("$date" -> Json.obj("$numberLong" -> "1517443200000"))
  val jsonEndFromMongo: JsObject = Json.obj("$date" -> Json.obj("$numberLong" -> "1517463200000"))

  val channels = Seq(GBDepartures, XIDepartures, GBArrivals, XIArrivals, Web, XML)
  val channelsJson = Seq(JsString("GB Departures"), JsString("XI Departures"), JsString("GB Arrivals"),
    JsString("XI Arrivals"), JsString("Web channel"), JsString("XML channel"))

  "DowntimeResponseReads" should {
    "return a DowntimeResponse when status is OK and can be parsed" in {

      for ((channel, index) <- channelsJson.zipWithIndex) {
        val expectedResult = DowntimeResponse(
          Seq(Downtime(channels(index), dateStart, dateEnd)),
          LocalDateTime.of(2022, 1, 1, 10, 25, 55)
        )
        val httpResponse = HttpResponse(Status.OK, json(channel))
        val Right(result) = DowntimeResponseReads.read("GET", "url", httpResponse)

        println("")
        println(result)
        result mustBe expectedResult
      }
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
    }
  }

  def json(channel: JsString): String = {
    s"""
       |{"downtimes":[
       |  { "affectedChannel":$channel,
       |    "start":$jsonStartFromMongo,
       |    "end":$jsonEndFromMongo
       |  }
       |],
       |"createdTs":"2022-01-01T10:25:55"
       |}""".stripMargin
  }
}
