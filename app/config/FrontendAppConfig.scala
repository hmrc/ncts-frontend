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

package config

import com.google.inject.{Inject, Singleton}
import com.typesafe.config.ConfigList
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.RequestHeader
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration, servicesConfig: ServicesConfig) {

  val host: String    = configuration.get[String]("host")
  val appName: String = configuration.get[String]("appName")

  private val contactHost                  = configuration.get[String]("contact-frontend.host")
  private val contactFormServiceIdentifier = "ncts-frontend"

  def feedbackUrl(implicit request: RequestHeader): String =
    s"$contactHost/contact/beta-feedback?service=$contactFormServiceIdentifier&backUrl=${SafeRedirectUrl(host + request.uri).encodedUrl}"

  val feedbackFrontendUrl: String = configuration.get[String]("feedback-frontend.url")

  val languageTranslationEnabled: Boolean = configuration.get[Boolean]("features.welsh-translation")

  private val nctsServiceName = "ncts"
  val nctsUrl: String         = s"${servicesConfig.baseUrl(nctsServiceName)}/$nctsServiceName"

  val languageMap: Map[String, Lang] = Map(
    "en" -> Lang("en")
  )

  val plannedDowntimesConfig: Option[ConfigList] = configuration.getOptional[ConfigList]("planned-downtime")

  val govUKOtherIssuesLink: String = configuration.get[String]("govUKLinks.govUKOtherIssuesLink")

  val europaLink: String = configuration.get[String]("externalLinks.europaLink")

  val govUKHomeLink: String = configuration.get[String]("govUKLinks.govUKHomeLink")

  val govUKTransitManualLink: String = configuration.get[String]("govUKLinks.govUKTransitManualLink")

  val govUKNCTSGuidanceLink: String = configuration.get[String]("govUKLinks.govUKNCTSGuidanceLink")

}
