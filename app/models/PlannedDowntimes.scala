/*
 * Copyright 2023 HM Revenue & Customs
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

case class PlannedDowntimes(plannedDowntimes: Seq[PlannedDowntime]) {

  def isArrival(downtime: PlannedDowntime): Boolean =
    downtime.affectedChannel.equals(GBArrivals) || downtime.affectedChannel.equals(XIArrivals)

  def isDeparture(downtime: PlannedDowntime): Boolean =
    downtime.affectedChannel.equals(GBDepartures) || downtime.affectedChannel.equals(XIDepartures)

  def arrivalsAffected: Boolean = plannedDowntimes.exists(isArrival)

  def departuresAffected: Boolean = plannedDowntimes.exists(isDeparture)

  def arrivalDowntimes: PlannedDowntimes = PlannedDowntimes(plannedDowntimes.filter(isArrival))

  def departureDowntimes: PlannedDowntimes = PlannedDowntimes(plannedDowntimes.filter(isDeparture))
}
