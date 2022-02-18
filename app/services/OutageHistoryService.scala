package services

import com.google.inject.Inject
import connectors.NCTSConnector
import models.responses.{DowntimeResponse, ErrorResponse}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class OutageHistoryService @Inject()(nctsConnector: NCTSConnector) {

  def checkOutageHistory()(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, DowntimeResponse]] =
    nctsConnector.checkOutageHistory
}
