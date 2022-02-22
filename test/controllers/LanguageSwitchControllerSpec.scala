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

import base.SpecBase
import config.FrontendAppConfig
import play.api.i18n.Lang
import play.api.mvc.ControllerComponents
import play.api.test.Injecting
import uk.gov.hmrc.play.language.LanguageUtils

class LanguageSwitchControllerSpec extends SpecBase with Injecting {

  "LanguageSwitchController" - {

    lazy val langSwitchController = new LanguageSwitchController(
      inject[FrontendAppConfig], inject[LanguageUtils], inject[ControllerComponents]
    )

    "languageMap" - {

      "must return the language map from config" in {

        langSwitchController.languageMap mustBe Map(
          "en" -> Lang("en")
        )
      }
    }

    "fallbackUrl" - {
      "must return the url for the status check page " in {
        langSwitchController.fallbackURL mustBe "/new-computerised-transit-system-service-availability/service-availability"
      }
    }
  }
}
