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
import play.api.http.Status
import play.api.test.Helpers.ACCEPTED
import uk.gov.hmrc.http.HeaderCarrier
import utils.SpecCommonHelper

class NctsConnectorISpec extends SpecCommonHelper {

  class Setup {
    val connector = app.injector.instanceOf[NctsConnector]
  }

  "check status" should {
    /*      "return AddressLookupOnRamp(modulusUrl)" in new Setup {

            val res = connector.checkStatus

            whenReady(res) { result =>
              result mustBe Right(AddressLookupOnRamp("/foo"))
            }
          }*/

        "return AddressLookupOnRamp(modulusUrl)" in new Setup {

            val res = connector.checkStatus().futureValue

          }

/*    "return the service status of the Web channel" in {

      wireMock.wireMockServer.stubFor(
        get(urlMatching("/service-status"))
          .willReturn(
            aResponse()
              .withStatus(Status.OK)
              .withBody("")
          )
      )
      val serviceStatus = connector.checkStatus()

    }*/
  }


}
