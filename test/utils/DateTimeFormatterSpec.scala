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

package utils

import base.SpecBase

import java.time.LocalDateTime

class DateTimeFormatterSpec extends SpecBase {

  "formatDateTimeKnownIssue" - {
    "should return the time with date if its prior to today" in {
      val localDateTime = LocalDateTime.of(2022, 1, 1, 10, 50, 50)

      val knownIssueSince = DateTimeFormatter.formatDateTimeKnownIssues(localDateTime)

      knownIssueSince mustBe "10:50am, 1 January 2022"
    }
    "should return just the time if its today" in {
      val now = LocalDateTime.now().withHour(10).withMinute(10)

      val knownIssueSince = DateTimeFormatter.formatDateTimeKnownIssues(now)

      knownIssueSince mustBe "10:10am"
    }
  }
}
