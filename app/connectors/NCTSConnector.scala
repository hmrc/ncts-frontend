/*
 * Copyright 2023 HM Revenue & Customs
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
import models.responses.{DowntimeResponse, StatusResponse}
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, UpstreamErrorResponse}

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NCTSConnector @Inject() (
  httpClient: HttpClient,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext) {

  def checkStatus()(implicit hc: HeaderCarrier): Future[Option[StatusResponse]] =
    makeGetCall[StatusResponse]("/status-check")

  def getDowntimeHistory()(implicit hc: HeaderCarrier): Future[Option[DowntimeResponse]] =
    makeGetCall[DowntimeResponse]("/downtime-history")

  private def makeGetCall[A](requestUrl: String)(implicit hc: HeaderCarrier, reads: Reads[A]): Future[Option[A]] =
    httpClient.GET[HttpResponse](s"${config.nctsUrl}$requestUrl") map { response =>
      response.status match {
        case OK        => validateJsonResponse[A](response.json, requestUrl)
        case NOT_FOUND => None
        case _         => throwError(response, requestUrl)
      }
    }

  private def validateJsonResponse[A](json: JsValue, requestUrl: String)(implicit reads: Reads[A]): Option[A] =
    json.validateOpt[A] match {
      case JsSuccess(value, _) => value
      case JsError(errors)     =>
        throw new RuntimeException(s"[NCTSConnector] - Could not parse json for $requestUrl: $errors")
    }

  private def throwError(response: HttpResponse, requestUrl: String) =
    throw UpstreamErrorResponse(
      s"[NCTSConnector]: $requestUrl resulted in an error - ${response.body}",
      response.status,
      response.status,
      response.headers
    )
}
