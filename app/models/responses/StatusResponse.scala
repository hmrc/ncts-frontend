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

package models.responses

import models.responses.ErrorResponse.StatusResponseError
import org.slf4j.LoggerFactory
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsSuccess, Json, Reads}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

case class StatusResponse(departuresApiHealthy: Boolean)

object StatusResponse {

  private val logger = LoggerFactory.getLogger(classOf[StatusResponse])

  implicit val reads: Reads[StatusResponse] = Json.reads[StatusResponse]

  implicit object StatusResponseReads extends HttpReads[Either[ErrorResponse, StatusResponse]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponse, StatusResponse] =
      response.status match {
        case OK =>
          response.json.validate[StatusResponse] match {
            case JsSuccess(model, _) =>
              Right(model)
            case JsError(error) =>
              val errorMessage = error.flatMap(_._2.map(_.message)).mkString("\n")
              logger.error(s"Error parsing StatusResponse: $errorMessage")
              Left(StatusResponseError(s"Response in an unexpected format: $errorMessage"))
          }
        case status =>
          logger.error(s"Error retrieving StatusResponse : Status '$status' \n ${response.body}")
          Left(StatusResponseError(s"Unexpected error occurred when checking service status: ${response.body}"))
      }
  }
}
