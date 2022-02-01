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

package views

import base.SpecBase
import models.responses.StatusResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Injecting
import views.html.ServiceAvailability

import java.time.LocalDateTime

class ServiceAvailabilityViewSpec extends SpecBase with Injecting {

  val view: ServiceAvailability = inject[ServiceAvailability]
  val document: Document = Jsoup.parse(view(
    StatusResponse(
      gbDeparturesHealthy = true,
      xiDeparturesHealthy = true,
      gbArrivalsHealthy = true,
      xiArrivalsHealthy = true,
      apiChannelHealthy = true,
      webChannelHealthy = true,
      LocalDateTime.of(2022, 1, 24, 0, 0, 0)
    )).body)

  "ServiceAvailability" - {

    "should have the correct breadcrumbs" in {
      breadcrumb(0, document).text() mustBe "Home"
      breadcrumb(0, document).attr("href") mustBe govukHomeLink
      breadcrumb(1, document).text() mustBe "Service availability and Planned downtime"
      breadcrumb(1, document).attr("href") mustBe homeLink
      breadcrumb(2, document).text() mustBe "Service availability"
      breadcrumb(2, document).attr("href") mustBe serviceAvailabilityLink
    }

    "should have the correct title" in {
      document.title() mustBe "Service availability - Service availability and Planned downtime - GOV.UK"
    }

    "should have the correct headings" in {
      document.getElementsByTag("caption").first()
        .text() must (fullyMatch regex s"""${messages("service.availability.status.arrivals")}(.+)""")

      document.getElementsByTag("caption").get(1)
        .text() must (fullyMatch regex s"""${messages("service.availability.status.departures")}(.+)""")

      document.getElementsByTag("caption").get(2)
        .text() must (fullyMatch regex s"""${messages("service.availability.submission.channels.status.heading")}(.+)""")
    }

    "should have a table for arrivals" in {
      document.getElementsByClass("govuk-table__header").first()
        .text() mustBe messages("service.availability.submission.channels.status.core")

      document.getElementsByClass("govuk-table__header").get(1)
        .text() mustBe messages("service.availability.system.availability")

      document.getElementsByClass("govuk-table__cell").first()
        .text() mustBe messages("service.availability.ncts.gb.arrivals")

      document.getElementsByClass("govuk-table__cell").get(2)
        .text() mustBe messages("service.availability.ncts.xi.arrivals")

    }

    "should have a table for departures" in {
      document.getElementsByClass("govuk-table__header").get(2)
        .text() mustBe messages("service.availability.submission.channels.status.core")

      document.getElementsByClass("govuk-table__header").get(3)
        .text() mustBe messages("service.availability.system.availability")

      document.getElementsByClass("govuk-table__cell").get(4)
        .text() mustBe messages("service.availability.ncts.gb.departures")

      document.getElementsByClass("govuk-table__cell").get(6)
        .text() mustBe messages("service.availability.ncts.xi.departures")
    }

    "should have a table for other systems" in {
      document.getElementsByClass("govuk-table__header").get(4)
        .text() mustBe messages("service.availability.submission.channels.status.core")

      document.getElementsByClass("govuk-table__header").get(5)
        .text() mustBe messages("service.availability.system.availability")

      document.getElementsByClass("govuk-table__cell").get(8)
        .text() mustBe messages("service.availability.submission.channels.status.web.channel")

      document.getElementsByClass("govuk-table__cell").get(10)
        .text() mustBe messages("service.availability.submission.channels.status.xml.channel")
    }

    "when all services are healthy" - {
      "should show that services are available for arrivals" in {
        document.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.available")
        document.getElementsByClass("govuk-table__cell").get(3)
          .text() mustBe messages("service.availability.status.available")
      }

      "should show that services are available for departures" in {
        document.getElementsByClass("govuk-table__cell").get(5)
          .text() mustBe messages("service.availability.status.available")
        document.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.available")
      }

      "should show that services are available for other systems" in {
        document.getElementsByClass("govuk-table__cell").get(9)
          .text() mustBe messages("service.availability.status.available")
        document.getElementsByClass("govuk-table__cell").get(11)
          .text() mustBe messages("service.availability.status.available")
      }
    }

    "when all services are not healthy" - {

      val allUnhealthyView: Document = Jsoup.parse(view(
        StatusResponse(
          gbDeparturesHealthy = false,
          xiDeparturesHealthy = false,
          gbArrivalsHealthy = false,
          xiArrivalsHealthy = false,
          apiChannelHealthy = false,
          webChannelHealthy = false,
          LocalDateTime.of(2022, 1, 24, 0, 0, 0)
        )).body)

      "should show that services have known issues for arrivals" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(1)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(3)
          .text() mustBe messages("service.availability.status.issues")
      }

      "should show that services have known issues for departures" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(5)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(7)
          .text() mustBe messages("service.availability.status.issues")
      }

      "should show that services have known issues for other systems" in {
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(9)
          .text() mustBe messages("service.availability.status.issues")
        allUnhealthyView.getElementsByClass("govuk-table__cell").get(11)
          .text() mustBe messages("service.availability.status.issues")
      }
    }
  }
}
