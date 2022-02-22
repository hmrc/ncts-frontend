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

package connectors

import com.google.inject.Inject
import config.FrontendAppConfig
import models.responses.{Downtime, DowntimeResponse, ErrorResponse, StatusResponse}
import models._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import java.time.LocalDateTime
import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class NCTSConnector @Inject()(
                               httpClient: HttpClient,
                               config: FrontendAppConfig
                             ) {

  def checkStatus()(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, StatusResponse]] =
    httpClient.GET[Either[ErrorResponse, StatusResponse]](s"${config.nctsUrl}/status-check")

  def checkOutageHistory()(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, DowntimeResponse]] = {
    val url = ""

    // httpClient.GET[Either[ErrorResponse, DowntimeResponse]](url)
    Future(Right(DowntimeResponse(
      Seq(
        Downtime(GBDepartures, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
        Downtime(XIDepartures, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
        Downtime(GBArrivals, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
        Downtime(XIArrivals, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
        Downtime(Web, LocalDateTime.now().minusHours(1), LocalDateTime.now()),
        Downtime(XML, LocalDateTime.now().minusHours(1), LocalDateTime.now())
      ), LocalDateTime.now()
    )
    )
    )
  }
}
