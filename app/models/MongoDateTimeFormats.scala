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

import play.api.libs.json._

import java.time.{Instant, LocalDateTime, ZoneId}

trait MongoDateTimeFormats {

  implicit val localDateTimeRead: Reads[LocalDateTime] =
    (__ \ "$date" \ "$numberLong").read[String].map {
      millis =>
        LocalDateTime.ofInstant(Instant.ofEpochMilli(millis.toLong), ZoneId.of("Europe/London"))
    }

  implicit val localDateTimeWrite: Writes[LocalDateTime] = (dateTime: LocalDateTime) => Json.obj(
    "$date" -> dateTime.atZone(ZoneId.of("Europe/London")).toInstant.toEpochMilli
  )
}

object MongoDateTimeFormats extends MongoDateTimeFormats
