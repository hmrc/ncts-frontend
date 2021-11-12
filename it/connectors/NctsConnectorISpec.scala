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

package connectors

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import models.responses.StatusResponse
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.OK
import utils.SpecCommonHelper

class NctsConnectorISpec extends SpecCommonHelper {

  "check status" should {

    "return OK with the correct view for a successful response when service is healthy" in {

      stubGet("/ncts/status-check", OK, Json.toJson(StatusResponse(departuresWebHealthy = true)).toString)

      val response = ws.url(s"${baseUrl}/service-availability").get()

      whenReady(response) { result =>
        result.status mustBe OK
        result.body must include(messages("service.availability.heading"))
        result.body must include(messages("service.availability.web.channel.card.available"))
        result.body must include(messages("service.availability.web.channel.card.available.description"))
      }
    }

    "return OK with the correct view for a successful response when service is not healthy" in {

      stubGet("/ncts/status-check", OK, Json.toJson(StatusResponse(departuresWebHealthy = false)).toString)

      val response = ws.url(s"${baseUrl}/service-availability").get()

      whenReady(response) { result =>
        result.status mustBe OK
        result.body must include(messages("service.availability.heading"))
        result.body must include(messages("service.availability.web.channel.card.unavailable.description"))
        result.body must include(messages("service.availability.web.channel.card.unavailable"))
      }
    }
  }
}
