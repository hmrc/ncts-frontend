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
import models.DowntimeHistoryRow
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.DowntimeHistoryService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DowntimeHistoryView

import scala.concurrent.{ExecutionContext, Future}

class DowntimeHistoryController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  outageHistoryService: DowntimeHistoryService,
  errorHandler: ErrorHandler,
  view: DowntimeHistoryView
)(implicit
  ec: ExecutionContext
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    outageHistoryService.getDowntimeHistory().flatMap {
      case Some(downtimeHistory: Seq[DowntimeHistoryRow]) =>
        Future.successful(Ok(view(downtimeHistory)))
      case _                                              =>
        Future.successful(errorHandler.showInternalServerError)
    }
  }
}
