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
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.test.Injecting
import views.html.IndexView

class IndexViewSpec extends SpecBase with Injecting {

  val view: IndexView = inject[IndexView]
  val document: Document = Jsoup.parse(view().body)

  "IndexView" - {

    "should have the correct title" in {
      document.title() mustBe "Home - Service availability and Planned downtime - GOV.UK"
    }

    "should have a heading caption" in {
      document.body().getElementsByClass("govuk-caption-xl").first()
        .text() mustBe "Guidance"
    }

    "should have the correct heading" in {
      document.body().getElementsByTag("h1").first()
        .text() mustBe "New Computerised Transit System (NCTS): service availability and planned downtime"
    }

    "should have a link to the service availability page" in {
      document.body().select(".govuk-grid-column-one-third > a ").first()
        .attr("href") mustBe "/new-computerised-transit-system-service-availability-and-issues/service-availability"
    }

    "should have a link to the planned downtime page" in {
      document.body().select(".govuk-grid-column-one-third > a ").get(1)
        .attr("href") mustBe "/new-computerised-transit-system-service-availability-and-issues/planned-downtime"
    }

    "should have other services content" in {
      document.body().getElementsByTag("h2").get(2)
        .text() mustBe "Other services"
    }

    "should have text about tracking issues in other services" in {
      document.body().getElementsByClass("govuk-body").first()
        .text() mustBe "Track availability and issues for other services."
    }

    "should a link for tracking issues in other services" in {
      document.body().select(".govuk-body > a ").first()
        .attr("href") mustBe "https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues"
    }

  }
}
