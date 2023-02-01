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

package controllers

import base.SpecBase
import play.api.test.FakeRequest
import play.api.test.Helpers._

class PlannedDowntimeControllerSpec extends SpecBase {

  "PlannedDowntimeController" - {

    "must show downtime configuration if it is not configured" in {

      val application = applicationBuilder().configure(Map("planned-downtime" -> Seq())).build()

      running(application) {

        val request = FakeRequest(GET, routes.PlannedDowntimeController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe OK
      }
    }

    "must show the internal server error page if planned downtime is configured incorrectly" in {

      val application = applicationBuilder().configure(Map("planned-downtime" -> Seq("incorrect"))).build()

      running(application) {

        val request = FakeRequest(GET, routes.PlannedDowntimeController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
