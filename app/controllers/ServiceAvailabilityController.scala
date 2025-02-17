/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers

import handlers.ErrorHandler
import models.PlannedDowntimeViewModel
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{HealthCheckService, PlannedDowntimeService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ServiceAvailability

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ServiceAvailabilityController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  healthCheckService: HealthCheckService,
  plannedDowntimeService: PlannedDowntimeService,
  errorHandler: ErrorHandler,
  view: ServiceAvailability
)(implicit
  ec: ExecutionContext
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    healthCheckService.checkStatus().flatMap {
      case Some(statusResponse) =>
        val plannedDowntimeViewModel: PlannedDowntimeViewModel =
          PlannedDowntimeViewModel
            .fromPlannedDowntimes(plannedDowntimeService.getPlannedDowntime(forPlannedDowntime = false))
        Future.successful(Ok(view(statusResponse, plannedDowntimeViewModel)))
      case _                    => errorHandler.showInternalServerError
    }
  }
}
