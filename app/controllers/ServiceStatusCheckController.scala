/*
 * Copyright 2021 HM Revenue & Customs
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

import models.responses.StatusResponse
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.NctsService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndexView

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ServiceStatusCheckController @Inject()(
                                              val controllerComponents: MessagesControllerComponents,
                                              nctsService: NctsService,
                                              view: IndexView
                                            ) extends FrontendBaseController with I18nSupport {


  def onPageLoad(): Action[AnyContent] = Action.async { implicit request =>
    nctsService.checkStatus().flatMap {
      case Right(statusResponse: StatusResponse) => Future.successful(Ok(view())) //TODO: Pass statusResponse to the view to display the status
      case _ => Future.successful(InternalServerError)
    }
  }
}
