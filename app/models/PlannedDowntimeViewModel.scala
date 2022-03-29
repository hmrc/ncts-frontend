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

import models.responses.ErrorResponse.DowntimeConfigParseError

import scala.annotation.tailrec

case class PlannedDowntimeViewModel(
                                     gbArrivals: Option[PlannedDowntime],
                                     xiArrivals: Option[PlannedDowntime],
                                     gbDepartures: Option[PlannedDowntime],
                                     xiDepartures: Option[PlannedDowntime]
                                   )

object PlannedDowntimeViewModel {

  def default: PlannedDowntimeViewModel = {
    PlannedDowntimeViewModel(
      gbArrivals = None,
      xiArrivals = None,
      gbDepartures = None,
      xiDepartures = None
    )
  }

  def fromPlannedDowntimes(downtimes: Either[DowntimeConfigParseError, Option[PlannedDowntimes]]): PlannedDowntimeViewModel = {
    downtimes match {
      case Right(downtimes) =>
        val plannedDowntimes: Seq[PlannedDowntime] = downtimes.get.plannedDowntimes
        val defaultPlannedDowntime = PlannedDowntimeViewModel.default

        @tailrec
        def loop(plannedDowntimes: Seq[PlannedDowntime], index: Int, result: PlannedDowntimeViewModel): PlannedDowntimeViewModel = {

          if (index >= plannedDowntimes.size) result
          else {
            val newResult = plannedDowntimes(index).affectedChannel.toString match {
              case "gbArrivals" => result.copy(gbArrivals = Some(plannedDowntimes(index)))
              case "xiArrivals" => result.copy(xiArrivals = Some(plannedDowntimes(index)))
              case "gbDepartures" => result.copy(gbDepartures = Some(plannedDowntimes(index)))
              case "xiDepartures" => result.copy(xiDepartures = Some(plannedDowntimes(index)))
            }
            loop(plannedDowntimes, index + 1, newResult)
          }
        }

        loop(plannedDowntimes, 0, defaultPlannedDowntime)
      case Left(_) =>
        PlannedDowntimeViewModel.default
    }
  }
}
