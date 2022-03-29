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
import com.typesafe.config.{ConfigList, ConfigRenderOptions}
import config.FrontendAppConfig
import models.responses.ErrorResponse.DowntimeConfigParseError
import models.{PlannedDowntime, PlannedDowntimes}
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, Json}

import javax.inject.Singleton

@Singleton
class PlannedDowntimeService @Inject()(appConfig: FrontendAppConfig) extends Logging {

  def getPlannedDowntime: Either[DowntimeConfigParseError, Option[PlannedDowntimes]] = {
    appConfig.plannedDowntimesConfig.fold[Either[DowntimeConfigParseError, Option[PlannedDowntimes]]](Right(None)) {
      downtimeConfig: ConfigList =>
        try {
          Json.parse(downtimeConfig.render(ConfigRenderOptions.concise())).validate[Seq[PlannedDowntime]] match {
            case JsSuccess(downtimes, _) if downtimes.nonEmpty =>
              Right(Some(PlannedDowntimes(downtimes)))
            case JsSuccess(downtimes, _) if downtimes.isEmpty =>
              Right(None)
            case JsError(error) =>
              val errorMessage = error.flatMap(_._2.map(_.message)).mkString("\n")
              logger.error(s"Error parsing downtime config: $errorMessage")
              Left(DowntimeConfigParseError(s"Error parsing downtime config: $errorMessage"))
          }
        } catch {
          case ex: Throwable =>
            logger.error(s"[PlannedDowntimeService][getPlannedDowntime]" +
              s" Failed to parse planned downtime with message ${ex.getMessage}")
            Left(DowntimeConfigParseError(s"Exception thrown when trying to parse downtime config: ${ex.getMessage}"))
        }
    }
  }
}
