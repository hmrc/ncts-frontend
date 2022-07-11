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

import base.SpecBase
import connectors.NCTSConnector
import models._
import models.responses.ErrorResponse.DowntimeConfigParseError
import models.responses.{Downtime, DowntimeResponse}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.http.Status.INTERNAL_SERVER_ERROR
import uk.gov.hmrc.http.UpstreamErrorResponse

import java.time.{LocalDateTime, ZoneId, ZonedDateTime}
import scala.concurrent.Future

class DowntimeHistoryServiceSpec extends SpecBase {

  val nctsConnector: NCTSConnector = mock[NCTSConnector]
  val plannedDowntimeService: PlannedDowntimeService = mock[PlannedDowntimeService]
  val service: DowntimeHistoryService = new DowntimeHistoryService(nctsConnector, plannedDowntimeService)(ec)

  "getDowntimeHistory" - {
    "when there is no planned downtime" - {
      "should return a valid status response" in {

        when(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false)) thenReturn Right(None)

        when(nctsConnector.getDowntimeHistory()(any())) thenReturn
          Future(Some(
            DowntimeResponse(
              Seq(
                Downtime(
                  GBDepartures,
                  LocalDateTime.of(2022, 1, 1, 10, 25, 55),
                  LocalDateTime.of(2022, 1, 1, 10, 25, 55)
                )),
              LocalDateTime.of(2022, 1, 1, 10, 25, 55))))(ec)

        val result = service.getDowntimeHistory().futureValue

        result mustBe
          Some(
            Seq(
              DowntimeHistoryRow(
                Downtime(
                  GBDepartures,
                  LocalDateTime.of(2022, 1, 1, 10, 25, 55),
                  LocalDateTime.of(2022, 1, 1, 10, 25, 55)
                ),
                planned = false
              )
            )
          )
      }

      "should return None when" - {

        "getPlannedDowntime returns a DowntimeConfigParseError" in {

          when(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false)
          ) thenReturn Left(DowntimeConfigParseError("There was a problem"))

          when(nctsConnector.getDowntimeHistory()(any())) thenReturn
            Future(Some(
              DowntimeResponse(
                Seq(
                  Downtime(
                    GBDepartures,
                    LocalDateTime.of(2022, 1, 1, 10, 25, 55),
                    LocalDateTime.of(2022, 1, 1, 10, 25, 55)
                  )),
                LocalDateTime.of(2022, 1, 1, 10, 25, 55))))(ec)

          service.getDowntimeHistory().futureValue mustBe None
        }

        "getDowntimeHistory returns None" in {

          when(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false)) thenReturn Right(None)

          when(nctsConnector.getDowntimeHistory()(any())) thenReturn Future.successful(None)

          service.getDowntimeHistory().futureValue mustBe None
        }
      }

      "should throw when getDowntimeHistory throws an UpstreamErrorResponse" in {

        when(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false)) thenReturn Right(None)

        when(nctsConnector.getDowntimeHistory()(any())) thenReturn
          Future.failed(UpstreamErrorResponse("Something went wrong", INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR))

        service.getDowntimeHistory().failed.futureValue mustBe an[UpstreamErrorResponse]
      }
    }
  }

  "filterInvalidDowntimes" - {

    "should filter out the invalid downtimes" in {
      val start1 = LocalDateTime.of(2022, 3, 9, 0, 19, 17, 40)
      val start2 = LocalDateTime.of(2022, 3, 9, 14, 3, 17, 40)
      val start3 = LocalDateTime.of(2022, 3, 9, 14, 39, 17, 40)
      val start4 = LocalDateTime.of(2022, 3, 13, 1, 59, 17, 40)
      val start5 = LocalDateTime.of(2022, 3, 20, 0, 18, 17, 40)

      val end1 = LocalDateTime.of(2022, 3, 9, 0, 57, 17, 12)
      val end2 = LocalDateTime.of(2022, 3, 9, 14, 56, 17, 13)
      val end3 = LocalDateTime.of(2022, 3, 9, 15, 25, 17, 12)
      val end4 = LocalDateTime.of(2022, 3, 13, 2, 29, 17, 12)
      val end5 = LocalDateTime.of(2022, 3, 20, 0, 41, 17, 12)

      val downtimes = Seq(
        Downtime(GBArrivals, start1, end1),
        Downtime(GBArrivals, start2, end2),
        Downtime(GBArrivals, start3, end3),
        Downtime(GBArrivals, start4, end4),
        Downtime(GBArrivals, start5, end5)
      )

      val result = service.filterInvalidDowntimes(downtimes)
      result mustBe Seq(Downtime(GBArrivals, start2, end2), Downtime(GBArrivals, start3, end3))
    }
  }

  "isPlannedDowntime" - {

    val middayToday = ZonedDateTime.now(ZoneId.of("UTC"))
      .withHour(12)
      .withMinute(0)
      .withSecond(0)
      .withNano(0)
      .toLocalDateTime

    "should correctly identify a planned downtime from config" - {
      "when the downtime period matches the planned downtime period" in {

        when(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false)) thenReturn Right(Some(
          PlannedDowntimes(Seq(
            PlannedDowntime(
              startDate = middayToday.toLocalDate.minusDays(2),
              middayToday.toLocalTime,
              middayToday.toLocalDate.minusDays(1),
              middayToday.toLocalTime,
              affectedChannel = GBArrivals,
            )
          ))
        ))

        when(nctsConnector.getDowntimeHistory()(any())) thenReturn
          Future(Some(
            DowntimeResponse(
              Seq(
                Downtime(
                  GBArrivals,
                  middayToday.minusDays(2),
                  middayToday.minusDays(1)
                ),
                Downtime(
                  GBDepartures,
                  middayToday.minusDays(3),
                  middayToday.minusDays(2)
                ),
                Downtime(
                  XIDepartures,
                  middayToday.minusDays(4),
                  middayToday.minusDays(2)
                ),
                Downtime(
                  XIArrivals,
                  middayToday.minusDays(5),
                  middayToday.minusDays(2)
                )),
              middayToday.minusDays(1))))(ec)

        val result = service.getDowntimeHistory().futureValue

        result.get mustBe
          Seq(DowntimeHistoryRow(
            Downtime(
              GBArrivals,
              middayToday.minusDays(2),
              middayToday.minusDays(1)
            ), planned = true),
            DowntimeHistoryRow(
              Downtime(
                GBDepartures,
                middayToday.minusDays(3),
                middayToday.minusDays(2)
              ), planned = false),
            DowntimeHistoryRow(
              Downtime(
                XIDepartures,
                middayToday.minusDays(4),
                middayToday.minusDays(2)
              ), planned = false),
            DowntimeHistoryRow(
              Downtime(
                XIArrivals,
                middayToday.minusDays(5),
                middayToday.minusDays(2)
              ), planned = false)
          )
      }

      "when the downtime period starts 1 hour before the start of planned downtime period and ends during the planned downtime period" in {

        val plannedDowntime = Right(Some(
          PlannedDowntimes(Seq(
            PlannedDowntime(
              startDate = middayToday.toLocalDate.minusDays(2),
              middayToday.toLocalTime,
              middayToday.toLocalDate.minusDays(1),
              middayToday.toLocalTime,
              affectedChannel = GBArrivals,
            )
          ))
        ))

        when(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false)) thenReturn plannedDowntime

        when(nctsConnector.getDowntimeHistory()(any())) thenReturn
          Future(Some(
            DowntimeResponse(
              Seq(
                Downtime(
                  GBArrivals,
                  middayToday.minusDays(2).minusHours(1),
                  middayToday.minusDays(1).minusHours(2)
                )),
              middayToday.minusDays(1))))(ec)

        val result = service.getDowntimeHistory().futureValue

        result.get mustBe
          Seq(DowntimeHistoryRow(
            Downtime(
              GBArrivals,
              middayToday.minusDays(2).minusHours(1),
              middayToday.minusDays(1).minusHours(2)
            ), planned = true)
          )
      }

      "when the downtime period starts 1 hour before the start of planned downtime period and ends 1 hour after the planned downtime period" in {

        val plannedDowntime = Right(Some(
          PlannedDowntimes(Seq(
            PlannedDowntime(
              startDate = middayToday.toLocalDate.minusDays(2),
              middayToday.toLocalTime,
              middayToday.toLocalDate.minusDays(1),
              middayToday.toLocalTime,
              affectedChannel = GBArrivals,
            )
          ))
        ))

        when(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false)) thenReturn plannedDowntime

        when(nctsConnector.getDowntimeHistory()(any())) thenReturn
          Future(Some(
            DowntimeResponse(
              Seq(
                Downtime(
                  GBArrivals,
                  middayToday.minusDays(2).minusHours(1),
                  middayToday.minusDays(1).plusHours(1)
                )),
              middayToday.minusDays(1))))(ec)

        val result = service.getDowntimeHistory().futureValue

        result.get mustBe
          Seq(DowntimeHistoryRow(
            Downtime(
              GBArrivals,
              middayToday.minusDays(2).minusHours(1),
              middayToday.minusDays(1).plusHours(1)
            ), planned = true)
          )
      }

      "when the downtime period starts 10 mins after the start of planned downtime period and ends 10 mins before the end of the planned downtime period" in {

        val plannedDowntime = Right(Some(
          PlannedDowntimes(Seq(
            PlannedDowntime(
              startDate = middayToday.toLocalDate.minusDays(2),
              middayToday.toLocalTime,
              middayToday.toLocalDate.minusDays(1),
              middayToday.toLocalTime,
              affectedChannel = GBArrivals,
            )
          ))
        ))

        when(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false)) thenReturn plannedDowntime

        when(nctsConnector.getDowntimeHistory()(any())) thenReturn
          Future(Some(
            DowntimeResponse(
              Seq(
                Downtime(
                  GBArrivals,
                  middayToday.minusDays(2).plusMinutes(10),
                  middayToday.minusDays(1).minusMinutes(10)
                )),
              middayToday.minusDays(1))))(ec)

        val result = service.getDowntimeHistory().futureValue

        result.get mustBe
          Seq(DowntimeHistoryRow(
            Downtime(
              GBArrivals,
              middayToday.minusDays(2).plusMinutes(10),
              middayToday.minusDays(1).minusMinutes(10)
            ), planned = true)
          )
      }
    }
  }
}
