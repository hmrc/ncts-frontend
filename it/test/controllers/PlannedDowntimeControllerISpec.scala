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

package test.controllers

import com.typesafe.config.ConfigFactory
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import play.api.Configuration
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import test.utils.SpecCommonHelper

import java.io.File
import scala.concurrent.Await

class PlannedDowntimeControllerISpec extends SpecCommonHelper {

  override implicit lazy val app = {

    val downtimeConfig = ConfigFactory.parseFile(new File("it/config/planned-downtime.conf"))

    new GuiceApplicationBuilder()
      .configure(Configuration(downtimeConfig))
      .build()
  }

  "planned-downtime" should {

    val response = ws.url(s"$baseUrl/planned-downtime").get()

    val result = Await.result(response, 2.seconds)

    "return OK" in {
      result.status mustBe OK
    }
  }

}
