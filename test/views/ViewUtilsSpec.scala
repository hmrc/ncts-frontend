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

package views

import base.SpecBase

class ViewUtilsSpec extends SpecBase {

  "ViewUtils" - {
    "title should produce a title in the format 'page name - service name - gov uk'" in {
      val application = applicationBuilder().build()
      ViewUtils.title("NCTS")(messages(application)) mustBe "NCTS - Service availability - GOV.UK"
      ViewUtils.title("test")(messages(application)) mustBe "test - NCTS service availability - GOV.UK"
    }

    "headingFromTitle should produce a heading from the title" in {
      val application = applicationBuilder().build()
      ViewUtils.headingFromTitle("testTime")(messages(application)) mustBe "testTime"
    }

  }
}
