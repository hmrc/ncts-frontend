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
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.{LocalDate, LocalTime}

class PlannedDowntimeViewModelSpec extends AnyWordSpec with Matchers {

  "fromPlannedDowntimes" should {

    "return a PlannedDowntimeViewModel with all fields populated" in {
      val date = LocalDate.now()
      val time = LocalTime.now()

      val plannedDowntimes: Seq[PlannedDowntime] =
        Seq(GBArrivals, XIArrivals, GBDepartures, XIDepartures).map(channel => createPlannedDowntime(date, time, date, time, channel))
      val downtimes = Right(Some(PlannedDowntimes(plannedDowntimes)))

      val result = PlannedDowntimeViewModel.fromPlannedDowntimes(downtimes)
      result mustBe PlannedDowntimeViewModel(
        Some(PlannedDowntime(date, time, date, time, GBArrivals)),
        Some(PlannedDowntime(date, time, date, time, XIArrivals)),
        Some(PlannedDowntime(date, time, date, time, GBDepartures)),
        Some(PlannedDowntime(date, time, date, time, XIDepartures))
      )
    }

    "return a PlannedDowntimeViewModel with arrival fields as None" in {
      val date = LocalDate.now()
      val time = LocalTime.now()

      val plannedDowntimes: Seq[PlannedDowntime] =
        Seq(GBDepartures, XIDepartures).map(channel => createPlannedDowntime(date, time, date, time, channel))
      val downtimes = Right(Some(PlannedDowntimes(plannedDowntimes)))

      val result = PlannedDowntimeViewModel.fromPlannedDowntimes(downtimes)
      result mustBe PlannedDowntimeViewModel(
        None,
        None,
        Some(PlannedDowntime(date, time, date, time, GBDepartures)),
        Some(PlannedDowntime(date, time, date, time, XIDepartures))
      )
    }

    "return default PlannedDowntimeViewModel when there arent any planned downtimes" in {
      val plannedDowntimes: Seq[PlannedDowntime] = Seq.empty
      val downtimes = Right(Some(PlannedDowntimes(plannedDowntimes)))

      val result = PlannedDowntimeViewModel.fromPlannedDowntimes(downtimes)
      result mustBe PlannedDowntimeViewModel(
        None,
        None,
        None,
        None
      )
    }

    "return default PlannedDowntimeViewModel when given None" in {

      val result = PlannedDowntimeViewModel.fromPlannedDowntimes(Right(None))
      result mustBe PlannedDowntimeViewModel(
        None,
        None,
        None,
        None
      )
    }

    "return default PlannedDowntimeViewModel when there are errors" in {
      val downtimes = Left(DowntimeConfigParseError("something went wrong"))

      val result = PlannedDowntimeViewModel.fromPlannedDowntimes(downtimes)
      result mustBe PlannedDowntimeViewModel(
        None,
        None,
        None,
        None
      )
    }
  }

  def createPlannedDowntime(startDate: LocalDate, startTime: LocalTime,
                            endDate: LocalDate, endTime: LocalTime, affectedChannel: Channel) = {
    PlannedDowntime(startDate, startTime, endDate, endTime, affectedChannel)
  }
}
