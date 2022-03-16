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

import models.responses.Downtime
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDateTime

class DowtimeSpec extends AnyWordSpec with Matchers {

  "filterInvalidDowntimes" should {

    "should filter out the invalid downtimes" in {
      val start1 = LocalDateTime.of(2022, 3, 9, 12, 19, 17, 40)
      val start2 = LocalDateTime.of(2022, 3, 9, 14, 3, 17, 40)
      val start3 = LocalDateTime.of(2022, 3, 9, 14, 39, 17, 40)
      val start4 = LocalDateTime.of(2022, 3, 13, 1, 59, 17, 40)

      val end1 = LocalDateTime.of(2022, 3, 9, 12, 57, 17, 12)
      val end2 = LocalDateTime.of(2022, 3, 9, 14, 56, 17, 13)
      val end3 = LocalDateTime.of(2022, 3, 9, 15, 25, 17, 12)
      val end4 = LocalDateTime.of(2022, 3, 13, 2, 29, 17, 12)

      val downtimes = Seq(
        Downtime(GBArrivals, start1, end1),
        Downtime(GBArrivals, start2, end2),
        Downtime(GBArrivals, start3, end3),
        Downtime(GBArrivals, start4, end4)
      )

      val result = Downtime.filterInvalidDowntimes(downtimes)
      result mustBe Seq(Downtime(GBArrivals, start2, end2), Downtime(GBArrivals, start3, end3))
    }
  }
}
