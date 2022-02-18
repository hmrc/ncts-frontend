package controllers

import com.google.inject.Inject
import handlers.ErrorHandler
import models.responses.DowntimeResponse
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.OutageHistoryService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext.Implicits.global


class OutageHistoryController @Inject()(
                                         val controllerComponents: MessagesControllerComponents,
                                         outageHistoryService: OutageHistoryService,
                                         errorHandler: ErrorHandler
                                       ) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async { implicit request =>
    outageHistoryService.checkOutageHistory.flatMap {
      case Right(downtimeResponse: DowntimeResponse) => ???
      case _ => ???
    }
  }
}
