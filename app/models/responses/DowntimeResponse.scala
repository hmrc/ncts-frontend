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
}

case class DowntimeResponse(downtimes: Seq[Downtime], createdTs: LocalDateTime)

object DowntimeResponse {
  private val logger = LoggerFactory.getLogger(classOf[DowntimeResponse])

  implicit lazy val reads: Reads[DowntimeResponse] = {
    (
      (__ \ "downtimes").read[Seq[Downtime]] and
        (__ \ "createdTs").read[LocalDateTime]

      ) (DowntimeResponse.apply _)
  }
  implicit object DowntimeResponseReads extends HttpReads[Either[ErrorResponse, DowntimeResponse]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, DowntimeResponse] =
      response.status match {
        case OK =>
          response.json.validate[DowntimeResponse] match {
            case JsSuccess(model, _) =>
              println("RIGHT ::::::::")
              println("RIGHT ::::::::")
              println("RIGHT ::::::::")
              Right(model)
            case JsError(error) =>
              println("LEFT ::::::::")
              println("LEFT ::::::::")
              println("LEFT ::::::::" +  response.json)

              println("LEFT ::::::::")
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

