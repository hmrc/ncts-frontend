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

package controllers

import com.google.inject.Inject
import handlers.ErrorHandler
import models.responses.DowntimeResponse
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.DowntimeHistoryService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.OutageHistory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class DowntimeHistoryController @Inject()(
                                           val controllerComponents: MessagesControllerComponents,
                                           outageHistoryService: DowntimeHistoryService,
                                           errorHandler: ErrorHandler,
                                           view: OutageHistory
                                         ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    outageHistoryService.checkOutageHistory.flatMap {
      case Right(downtimeResponse: DowntimeResponse) =>
        Future.successful(Ok(view(downtimeResponse.downtimes)))
      case _ =>
        Future.successful(errorHandler.showInternalServerError)
    }
  }
}
