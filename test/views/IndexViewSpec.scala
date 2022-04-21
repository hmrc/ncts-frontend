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

    "should have the correct breadcrumbs" in {
      breadcrumb(0, document).text() mustBe "Home"
      breadcrumb(0, document).attr("href") mustBe govukHomeLink
      breadcrumb(1, document).text() mustBe "NCTS service availability"
      breadcrumb(1, document).attr("href") mustBe homeLink
    }

    "should have the correct title" in {
      document.title() mustBe "NCTS - Service availability - GOV.UK"
    }

    "should have the correct heading" in {
      document.body().getElementsByTag("h1").first()
        .text() mustBe "NCTS service availability"
    }

    "should have a link to the service availability page" in {
      document.body().select(".govuk-grid-column-one-third > a ").first()
        .attr("href") mustBe "/new-computerised-transit-system-service-availability/service-availability"
    }

    "should have a link to the planned downtime page" in {
      document.body().select(".govuk-grid-column-one-third > a ").get(1)
        .attr("href") mustBe "/new-computerised-transit-system-service-availability/planned-downtime"
    }

    "should have a link to the downtime history page" in {
      document.body().select(".govuk-grid-column-one-third > a ").get(2)
        .attr("href") mustBe "/new-computerised-transit-system-service-availability/downtime-history"
    }

    "should have other services content" in {
      document.body().getElementsByTag("h2").get(3)
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

    "should have text about tracking international movements" in {
      document.body().getElementsByClass("govuk-body").get(1)
        .text() mustBe "Track international movements using a MRN via the Europa website."
    }

    "should a link for tracking international movements" in {
      document.body().select(".govuk-body").get(1).select("a").first()
        .attr("href") mustBe "https://ec.europa.eu/taxation_customs/dds2/tra/transit_home.jsp"
    }

    "should have a get help link" in {
      document.body().select(".hmrc-report-technical-issue").first()
        .attr("href") mustBe getHelpUrl
    }
  }

}
