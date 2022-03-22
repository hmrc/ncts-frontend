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

package models.responses

import models.responses.ErrorResponse.DowntimeResponseError
import models.{DowntimeChannel, MongoDateTimeFormats}
import org.slf4j.LoggerFactory
import play.api.http.Status.OK
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import java.time.LocalDateTime

case class Downtime(affectedChannel: DowntimeChannel, start: LocalDateTime, end: LocalDateTime)

object Downtime {

  implicit val writes: Writes[Downtime] = {
    (
      (__ \ "affectedChannel").write[DowntimeChannel](DowntimeChannel.format) and
        (__ \ "start").write(MongoDateTimeFormats.localDateTimeWrite) and
        (__ \ "end").write(MongoDateTimeFormats.localDateTimeWrite)
      ) (unlift(Downtime.unapply))
  }

  implicit val reads: Reads[Downtime] = {
    (
      (__ \ "affectedChannel").read[DowntimeChannel](DowntimeChannel.format) and
        (__ \ "start").read(MongoDateTimeFormats.localDateTimeRead) and
        (__ \ "end").read(MongoDateTimeFormats.localDateTimeRead)
      ) (Downtime.apply _)
  }

  def filterInvalidDowntimes(downtimes: Seq[Downtime]): Seq[Downtime] = {
    val invalidDowntimeEnd1 = LocalDateTime.of(2022, 3, 9, 12, 57)
    val invalidDowntimeEnd2 = LocalDateTime.of(2022, 3, 13, 2, 29)
    val invalidDowntimeEnd3 = LocalDateTime.of(2022, 3, 20, 12, 41)
    val invalidDowntimeTimestamps = Seq(invalidDowntimeEnd1, invalidDowntimeEnd2, invalidDowntimeEnd3)

    downtimes.filterNot(downtime =>
      invalidDowntimeTimestamps.exists(_.equals(downtime.end.withSecond(0).withNano(0)))
    )
  }
}

case class DowntimeResponse(downtimes: Seq[Downtime], createdTs: LocalDateTime)

object DowntimeResponse {
  private val logger = LoggerFactory.getLogger(classOf[DowntimeResponse])

  implicit val format: OFormat[DowntimeResponse] = Json.format[DowntimeResponse]

  implicit object DowntimeResponseReads extends HttpReads[Either[ErrorResponse, DowntimeResponse]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, DowntimeResponse] =
      response.status match {
        case OK =>
          response.json.validate[DowntimeResponse] match {
            case JsSuccess(model, _) =>
              Right(model)
            case JsError(error) =>
              val errorMessage = error.flatMap(_._2.map(_.message)).mkString("\n")
              logger.error(s"Error parsing DowntimeResponse: $errorMessage")
              Left(DowntimeResponseError(s"Response in an unexpected format: $errorMessage"))
          }
        case status =>
          logger.error(s"Error retrieving DowntimeResponse : Status '$status' \n ${response.body}")
          Left(DowntimeResponseError(s"Unexpected error occurred when checking service status: ${response.body}"))
      }
  }
}

