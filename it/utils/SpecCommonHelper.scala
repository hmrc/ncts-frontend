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

package utils

import org.scalatest._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.i18n.{Lang, Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier

trait SpecCommonHelper
    extends PlaySpec
    with ScalaFutures
    with IntegrationPatience
    with WiremockHelper
    with GuiceOneServerPerSuite
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .configure(config)
    .build

  val mockHost = WiremockHelper.wiremockHost
  val mockPort = WiremockHelper.wiremockPort.toString
  val mockUrl  = s"http://$mockHost:$mockPort"

  val config: Map[String, Any] =
    Map[String, Any](
      "metrics.enabled"                 -> false,
      "auditing.consumer.baseUri.host"  -> mockHost,
      "auditing.consumer.baseUri.port"  -> mockPort,
      "microservice.services.ncts.host" -> mockHost,
      "microservice.services.ncts.port" -> mockPort
    )

  implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(Seq(Lang.defaultLang))

  protected val ws: WSClient = app.injector.instanceOf[WSClient]

  protected val baseUrl = s"http://localhost:$port/new-computerised-transit-system-service-availability"

  override def beforeEach(): Unit =
    resetWiremock()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }
}
