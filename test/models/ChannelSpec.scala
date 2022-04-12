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

package models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsError, JsString, JsValue}

class ChannelSpec extends AnyWordSpec with Matchers {

  "Channel" should {
    "return a JsError if the channel cannot be parsed from Json" in {
      val fakeChannel: JsValue = JsString("fakeChannelName")

      val expectedResult = JsError(s"Failed to construct Channel from value fakeChannelName")

      Channel(fakeChannel) mustBe expectedResult

    }

  }
}
