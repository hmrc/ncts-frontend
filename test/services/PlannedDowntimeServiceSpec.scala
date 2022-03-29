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
import com.typesafe.config.ConfigList
import config.FrontendAppConfig
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when

class PlannedDowntimeServiceSpec extends SpecBase {

  val mockAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val mockConfigList: ConfigList = mock[ConfigList]

  when(mockAppConfig.plannedDowntimesConfig).thenReturn(
    Some(mockConfigList)
  )

  val service = new PlannedDowntimeService(mockAppConfig)

  "plannedDowntime" - {
    "return a Left if an exception is thrown when the planned downtime json is invalid" in {

      when(mockConfigList.render(ArgumentMatchers.any())).thenReturn(
        """
          |]invalid-json]]{}{{}{
          |""".stripMargin)

      service.getPlannedDowntime.left.get.message must (fullyMatch regex "Exception thrown when trying to parse downtime config((.|\n)+)")
    }

    "return a Left if the planned downtime json is valid but cannot be parsed" in {

      when(mockConfigList.render(ArgumentMatchers.any())).thenReturn(
        """
          |"planned-downtime" [
          |    { "incorrectKey": "2021-03-15", "startTime": "08:15", "endDate": "2021-03-16", "endTime": "17:00", "affectedChannel": "gbArrivals" }
          |]
          |""".stripMargin)

      service.getPlannedDowntime.left.get.message must (fullyMatch regex "Error parsing downtime config((.|\n)+)")
    }

    "return a Right(None) if there is no config for planned-downtime" in {
      when(mockAppConfig.plannedDowntimesConfig).thenReturn(None)

      service.getPlannedDowntime mustBe Right(None)

    }
  }
}
