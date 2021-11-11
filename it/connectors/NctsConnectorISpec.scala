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

import com.github.tomakehurst.wiremock.client.WireMock._
import models.responses.StatusResponse
import play.api.libs.json.{Json, Writes}
import play.api.libs.ws.WSResponse
import play.api.test.Helpers.OK
import utils.SpecCommonHelper

class NctsConnectorISpec extends SpecCommonHelper {

  import scala.concurrent.duration._
  import scala.concurrent.{Await, Future}

  val connector = app.injector.instanceOf[NctsConnector]


  def jsonOf[A: Writes](obj: A): String =
    Json.toJson(obj).toString()

  implicit val defaultTimeout: FiniteDuration = 5 seconds

  implicit def extractAwait[A](future: Future[A]): A = await[A](future)

  def await[A](future: Future[A])(implicit timeout: Duration): A = Await.result(future, timeout)

  "check status" should {

    "return AddressLookupOnRamp(modulusUrl)" in {

      stubFor(
        get(
          urlEqualTo(
            "/ncts/status-check"
          )
        ).willReturn(
          aResponse()
            .withStatus(OK)
            .withBody(jsonOf(StatusResponse(online = true)))
        )
      )

      val response: WSResponse =
        await(ws.url(s"${baseUrl}/status-check").get())

      response.status mustBe OK
    }
  }
}
