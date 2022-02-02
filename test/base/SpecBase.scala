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

package base

import org.jsoup.nodes.{Document, Element}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{FakeRequest, Injecting}
import uk.gov.hmrc.http.HeaderCarrier

trait SpecBase
  extends AnyFreeSpec
    with Matchers
    with WsScalaTestClient
    with GuiceOneAppPerSuite
    with MockitoSugar
    with TryValues
    with OptionValues
    with ScalaFutures
    with IntegrationPatience
    with Injecting {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val fakeRequest: FakeRequest[_] = FakeRequest()

  lazy val messagesApi: MessagesApi = inject[MessagesApi]
  implicit lazy val messages: MessagesImpl = MessagesImpl(Lang("en-GB"), messagesApi)

  def messages(app: Application): Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides()

  def breadcrumb(index: Int, document: Document): Element = {
    document.body().getElementsByClass("govuk-breadcrumbs__link").get(index)
  }

  val homeLink = "/new-computerised-transit-system-service-availability-and-issues"

  val serviceAvailabilityLink = "/new-computerised-transit-system-service-availability-and-issues/service-availability"
  val plannedDowntimeLink = "/new-computerised-transit-system-service-availability-and-issues/planned-downtime"

  val govukHomeLink = "https://www.gov.uk/government/publications" +
    "/new-computerised-transit-system-service-availability-and-issues" +
    "/new-computerised-transit-system-service-availability-and-issues"
}
