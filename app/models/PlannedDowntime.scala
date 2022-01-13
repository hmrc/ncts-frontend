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

import play.api.libs.json.{Reads, __}

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalTime}

case class PlannedDowntime(startDate: LocalDate, startTime: LocalTime,
                           endDate: LocalDate, endTime: LocalTime, affectedChannel: Channel.Value)

object PlannedDowntime {

  import play.api.libs.functional.syntax._

  implicit val dateFormat: Reads[LocalDate] =
    Reads[LocalDate](js =>
      js.validate[String].map(
        LocalDate.parse(_, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    )

  implicit val timeFormat: Reads[LocalTime] =
    Reads[LocalTime](js =>
      js.validate[String].map(
        LocalTime.parse(_, DateTimeFormatter.ofPattern("HH:mm")))
    )

  implicit lazy val reads: Reads[PlannedDowntime] = (
    (__ \ "startDate").read[LocalDate] and
      (__ \ "startTime").read[LocalTime] and
      (__ \ "endDate").read[LocalDate] and
      (__ \ "endTime").read[LocalTime] and
      (__ \ "affectedChannel").read[Channel.Value]
    ) (PlannedDowntime.apply _)
}
