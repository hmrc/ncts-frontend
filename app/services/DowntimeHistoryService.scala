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

package services

import com.google.inject.Inject
import connectors.NCTSConnector
import models.responses.Downtime
import models.{DowntimeHistoryRow, PlannedDowntime, PlannedDowntimes}
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDateTime
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DowntimeHistoryService @Inject()(
                                        nctsConnector: NCTSConnector, plannedDowntimeService: PlannedDowntimeService
                                      )(implicit ec: ExecutionContext) {

  def getDowntimeHistory()(implicit hc: HeaderCarrier): Future[Option[Seq[DowntimeHistoryRow]]] = {

    nctsConnector.getDowntimeHistory() map { response =>
      response flatMap { history =>
        historyWithReasons(filterInvalidDowntimes(history.downtimes))
      }
    }
  }

  def filterInvalidDowntimes(downtimes: Seq[Downtime]): Seq[Downtime] = {
    val invalidDowntimeTimestamps = Seq(
      LocalDateTime.of(2022, 3, 9, 0, 57),
      LocalDateTime.of(2022, 3, 13, 2, 29),
      LocalDateTime.of(2022, 3, 20, 0, 41),
      LocalDateTime.of(2022, 3, 23, 13, 51)
    )

    downtimes.filterNot(downtime =>
      invalidDowntimeTimestamps.exists(_.equals(downtime.end.withSecond(0).withNano(0)))
    )
  }

  def historyWithReasons(downtimes: Seq[Downtime]): Option[Seq[DowntimeHistoryRow]] = {

    plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false) match {
      case Left(_) => None
      case Right(plannedDowntime) => Some(
        downtimes.foldLeft(Seq[DowntimeHistoryRow]()) {
          (downtimes, downtime) =>
            val isPlanned = isPlannedDowntime(downtime, plannedDowntime)
            downtimes :+ DowntimeHistoryRow(downtime, planned = isPlanned)
        })
    }
  }

  def isPlannedDowntime(downtime: Downtime, plannedDowntimes: Option[PlannedDowntimes]): Boolean = {

    def onOrAfter(downtime: LocalDateTime, plannedDowntime: LocalDateTime): Boolean = {
      downtime.isAfter(plannedDowntime) || downtime.isEqual(plannedDowntime)
    }

    def onOrBefore(downtime: LocalDateTime, plannedDowntime: LocalDateTime): Boolean = {
      downtime.isBefore(plannedDowntime) || downtime.isEqual(plannedDowntime)
    }

    if (plannedDowntimes.isDefined) {
      plannedDowntimes.get.plannedDowntimes.collect {
        case PlannedDowntime(startDate, startTime, endDate, endTime, affectedChannel) =>
          val plannedDowntimeStartWithBuffer = LocalDateTime.of(startDate, startTime).minusHours(1)
          val plannedDowntimeEndWithBuffer = LocalDateTime.of(endDate, endTime).plusHours(1)
          val downtimeStart = downtime.start
          val downtimeEnd = downtime.end

          onOrAfter(downtimeStart, plannedDowntimeStartWithBuffer) &&
            onOrBefore(downtimeEnd, plannedDowntimeEndWithBuffer) &&
            downtime.affectedChannel.toString.toLowerCase == affectedChannel.toString.toLowerCase
      }.contains(true)
    } else {
      false
    }
  }
}
