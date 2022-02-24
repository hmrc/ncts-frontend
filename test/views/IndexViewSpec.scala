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
      breadcrumb(1, document).text() mustBe "Service availability and Planned downtime"
      breadcrumb(1, document).attr("href") mustBe homeLink
    }

    "should have the correct title" in {
      document.title() mustBe "Home - Service availability and Planned downtime - GOV.UK"
    }

    "should have a heading caption" in {
      document.body().getElementsByClass("govuk-caption-xl").first()
        .text() mustBe "Guidance"
    }

    "should have the correct heading" in {
      document.body().getElementsByTag("h1").first()
        .text() mustBe "New Computerised Transit System (NCTS): Service availability and Planned downtime"
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
      document.body().getElementsByTag("h2").get(3)
        .text() mustBe "Other services"
    }

    "should have details about XML channel decommissioning" in {
      val gridRow = document.body().select("#main-content > div:nth-child(4) > div")
      gridRow.select("h2").text() mustBe "Planned decommissioning of NCTS legacy XML channel"
      gridRow.select("p").get(0).text() mustBe "Work to close the legacy XML channel for NCTS will start on" +
        " 28 February 2022. This will run until 11:59pm GMT on 13 March 2022 after which it will no longer be possible" +
        " to make a new declaration using this channel."
      gridRow.select("details > summary > span").text() mustBe "View more details"
      gridRow.select("details > div:nth-child(2)").text() mustBe "For users of the NCTS email channel the final" +
        " date to make declarations will be 31 May 2022, when we will also close this channel to new declarations."
      gridRow.select("details > div:nth-child(3)").text() mustBe "Users must move to the new" +
        " XML Application Programming Interface (API) channel to help manage post-Brexit volumes." +
        " Users should speak to their own software provider to ensure they are ready for this transition."
      gridRow.select("details > div:nth-child(4)").text() mustBe "If unable to move to the new XML API," +
        " users should submit by using either the free to use web portal or non-transit customs processes after" +
        " these channels are closed."
    }

    "should have text about tracking issues in other services" in {
      document.body().getElementsByClass("govuk-body").get(1)
        .text() mustBe "Track availability and issues for other services."
    }

    "should a link for tracking issues in other services" in {
      document.body().select(".govuk-body > a ").first()
        .attr("href") mustBe "https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues"
    }

    "should have a get help link" in {
      document.body().select(".hmrc-report-technical-issue").first()
        .attr("href") mustBe getHelpUrl
    }
  }

}
