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
import models.responses.{ErrorResponse, HealthDetails, StatusResponse}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import java.time.LocalDateTime
import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class NctsConnector @Inject()(
                               httpClient: HttpClient,
                               config: FrontendAppConfig
                             ) {

  def checkStatus()(implicit hc: HeaderCarrier): Future[Either[ErrorResponse, StatusResponse]] =


    Future.successful(Right(StatusResponse(
      gbDeparturesStatus = HealthDetails(false, LocalDateTime.now),
      xiDeparturesStatus = HealthDetails(false, LocalDateTime.of(2022, 1, 1, 10, 50, 50)),
      gbArrivalsStatus = HealthDetails(false, LocalDateTime.now),
      xiArrivalsStatus = HealthDetails(false, LocalDateTime.of(2022, 1, 1, 10, 50, 50)),
      xmlChannelStatus = HealthDetails(true, LocalDateTime.of(2022, 1, 1, 10, 50, 50)),
      webChannelStatus = HealthDetails(true, LocalDateTime.of(2022, 1, 1, 10, 50, 50)),
      createdTs = LocalDateTime.now()
    )))
  //httpClient.GET[Either[ErrorResponse, StatusResponse]](s"${config.nctsUrl}/status-check")

}
