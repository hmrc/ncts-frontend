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

import models._
import models.responses.ErrorResponse.StatusResponseError
import models.responses.StatusResponse.dateTimeOrdering
import org.slf4j.LoggerFactory
import play.api.http.Status.OK
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import utils.DateTimeFormatter

import java.time.{LocalDate, LocalDateTime}

case class ChannelKnownIssue(channel: Channel, issueSince: LocalDateTime, isBCP: Boolean = false, eta: Option[String] = None)

case class StatusResponse(
                           gbDeparturesStatus: HealthDetails,
                           xiDeparturesStatus: HealthDetails,
                           gbArrivalsStatus: HealthDetails,
                           xiArrivalsStatus: HealthDetails,
                           xmlChannelStatus: HealthDetails,
                           webChannelStatus: HealthDetails,
                           ppnStatus: HealthDetails,
                           timelineEntries: Seq[TimelineUpdate] = Nil,
                           createdTs: LocalDateTime
                         ) {
  def xmlAndWebHealthy: Boolean = xmlChannelStatus.healthy && webChannelStatus.healthy

  def xmlHealthy: Boolean = xmlChannelStatus.healthy

  def ppnNotHealthy: Boolean = !ppnStatus.healthy

  private def knownIssuesAndCorrespondingEtas(knownIssues: List[ChannelKnownIssue]): List[ChannelKnownIssue] = {
    val filteredEtas = knownIssues.flatMap(
      issue => timelineEntries.filter(_.channel == issue.channel)
      .map(_.toChannelWithKnownIssue))

    (knownIssues ++ filteredEtas).sortBy(_.issueSince).reverse
  }

  private def unhealthyChannels(channels: List[(HealthDetails, Channel)]): List[ChannelKnownIssue] = {
    channels.filter(!_._1.healthy)
      .map(ch => ChannelKnownIssue(ch._2, ch._1.statusChangedAt))
  }

  def arrivalsWithKnownIssuesAndEta: List[ChannelKnownIssue] = {
    val arrivalKnownIssues = unhealthyChannels(
      List((gbArrivalsStatus, GBArrivals),
        (xiArrivalsStatus, XIArrivals)))

    knownIssuesAndCorrespondingEtas(arrivalKnownIssues)
  }

  def departuresWithKnownIssuesAndEta: List[ChannelKnownIssue] = {
    val departureKnownIssues = unhealthyChannels(
      List((gbDeparturesStatus, GBDepartures),
        (xiDeparturesStatus, XIDepartures)))
    knownIssuesAndCorrespondingEtas(departureKnownIssues)
  }

  def channelsWithKnownIssuesAndEta: List[ChannelKnownIssue] = {
    val channelIssues = unhealthyChannels(
      List((xmlChannelStatus, XML),
        (webChannelStatus, Web),
        (ppnStatus, PPN)))
    knownIssuesAndCorrespondingEtas(channelIssues)
  }
}

object StatusResponse {

  private val logger = LoggerFactory.getLogger(classOf[StatusResponse])

  implicit val etaReads: Reads[TimelineUpdate] = TimelineUpdate.reads

  implicit lazy val reads: Reads[StatusResponse] = {

    implicit val healthDetailsReads: Reads[HealthDetails] = HealthDetails.format

    (
      (__ \ "gbDeparturesStatus").read[HealthDetails] and
        (__ \ "xiDeparturesStatus").read[HealthDetails] and
        (__ \ "gbArrivalsStatus").read[HealthDetails] and
        (__ \ "xiArrivalsStatus").read[HealthDetails] and
        (__ \ "xmlChannelStatus").read[HealthDetails] and
        (__ \ "webChannelStatus").read[HealthDetails] and
        (__ \ "ppnStatus").read[HealthDetails] and
        (__ \ "timelineEntries").read[Seq[TimelineUpdate]] and
        (__ \ "createdTs").read[LocalDateTime]

      ) (StatusResponse.apply _)
  }

  implicit lazy val writes: OWrites[StatusResponse] = {

    implicit val healthDetailsWrites: Writes[HealthDetails] = HealthDetails.format

    (
      (__ \ "gbDeparturesStatus").write[HealthDetails] and
        (__ \ "xiDeparturesStatus").write[HealthDetails] and
        (__ \ "gbArrivalsStatus").write[HealthDetails] and
        (__ \ "xiArrivalsStatus").write[HealthDetails] and
        (__ \ "xmlChannelStatus").write[HealthDetails] and
        (__ \ "webChannelStatus").write[HealthDetails] and
        (__ \ "ppnStatus").write[HealthDetails] and
        (__ \ "timelineEntries").write[Seq[TimelineUpdate]] and
        (__ \ "createdTs").write[LocalDateTime]

      ) (unlift(StatusResponse.unapply))

  }

  implicit val dateTimeOrdering: Ordering[LocalDateTime] = _ compareTo _

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

case class HealthDetails(healthy: Boolean, statusChangedAt: LocalDateTime, lastMessageAccepted: Option[LocalDateTime])

object HealthDetails {
  implicit val format: OFormat[HealthDetails] = Json.format[HealthDetails]
}

case class TimelineUpdate(
                           channel: Channel,
                           time: Option[String],
                           date: Option[LocalDate],
                           businessContinuityFlag: Boolean,
                           createdTs: LocalDateTime
                         ) {
  def toChannelWithKnownIssue: ChannelKnownIssue = {
    val dateTimeStr = (time ++ date.map(DateTimeFormatter.formatDateWithoutDayOfWeek)).reduceOption(_ + ", " + _)
    ChannelKnownIssue(channel, createdTs, businessContinuityFlag, dateTimeStr)
  }
}

object TimelineUpdate {
  implicit val writes: Writes[TimelineUpdate] = {
    (
      (__ \ "channel").write[Channel](Channel.format) and
        (__ \ "time").writeNullable[String] and
        (__ \ "date").writeNullable[LocalDate] and
        (__ \ "businessContinuityFlag").write[Boolean] and
        (__ \ "createdTs").write(MongoDateTimeFormats.DefaultLocalDateTimeWrites)
      ) (unlift(TimelineUpdate.unapply))
  }
  implicit val reads: Reads[TimelineUpdate] = {
    (
      (__ \ "channel").read[Channel](Channel.format) and
        (__ \ "time").readNullable[String] and
        (__ \ "date").readNullable[LocalDate] and
        (__ \ "businessContinuityFlag").read[Boolean].orElse(Reads.pure(false)) and
        (__ \ "createdTs").read(MongoDateTimeFormats.DefaultLocalDateTimeReads)
      ) (TimelineUpdate.apply _)
  }
}