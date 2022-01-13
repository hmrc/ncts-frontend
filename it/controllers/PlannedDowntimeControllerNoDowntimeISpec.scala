package controllers

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import utils.SpecCommonHelper

import scala.concurrent.Await

class PlannedDowntimeControllerNoDowntimeISpec extends SpecCommonHelper {

  override implicit lazy val app = {

    new GuiceApplicationBuilder()
      .configure(Map("planned-downtime" -> Seq()))
      .build
  }

  "planned-downtime" when {

    val response = ws.url(s"${baseUrl}/planned-downtime").get()

    val result = Await.result(response, 2.seconds)

    val document: Document = Jsoup.parse(result.body)

    "there is no downtime config" should {
      "return OK" in {
        result.status mustBe OK
      }

      "have the correct heading" in {
        document.select("h1").first().text() mustBe messages("service.planned-downtime.heading")
      }

      "not have any tables of downtime information" in {
        document.getElementsByTag("table").size() mustBe 0
      }

      "have the arrivals subheading" in {
        document.select("h2").first().text() mustBe messages("service.planned-downtime.ncts.arrivals")
      }

      "have text about there being no downtime for arrivals" in {
        document.getElementsByClass("govuk-body").first().text() mustBe messages("service.planned-downtime.no.downtime.planned.arrivals")
      }

      "have the departures subheading" in {
        document.select("h2").get(1).text() mustBe messages("service.planned-downtime.ncts.departures")
      }

      "have text about there being no downtime for departures" in {
        document.getElementsByClass("govuk-body").last().text() mustBe messages("service.planned-downtime.no.downtime.planned.departures")
      }
    }
  }
}
