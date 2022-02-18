package models.responses

import models.Channel
import models.Channel.Channel
import models.responses.ErrorResponse.DowntimeResponseError
import org.slf4j.LoggerFactory
import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsSuccess, Json, OFormat}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

import java.time.LocalDateTime

case class DowntimeResponse(downtimes: Seq[Downtime])

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

case class Downtime(affectedChannel: Channel.Value, start: LocalDateTime, end: LocalDateTime)

object Downtime {
  implicit val format = Json.format[Downtime]
}
