package models

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue}

sealed trait Channe

case object GBDepartures extends Channe
case object XIDepartures extends Channe
case object GBArrivals extends Channe
case object XIArrivals extends Channe
case object Web extends Channe
case object XML extends Channe

object Channe {

  def apply(Channe: JsValue): JsResult[Channe] = Channe.as[String] match {
    case "GB Departures" => JsSuccess(GBDepartures)
    case "XI Departures" => JsSuccess(XIDepartures)
    case "GB Arrivals" => JsSuccess(GBArrivals)
    case "XI Arrivals" => JsSuccess(XIArrivals)
    case "Web Channe" => JsSuccess(Web)
    case "XML Channe" => JsSuccess(XML)
    case value => JsError(s"Failed to construct Channe from value $value")
  }

  def unapply(Channe: Channe): JsString = Channe match {
    case GBDepartures => JsString("GB Departures")
    case XIDepartures => JsString("XI Departures")
    case GBArrivals => JsString("GB Arrivals")
    case XIArrivals => JsString("XI Arrivals")
    case Web => JsString("Web Channe")
    case XML => JsString("XML Channe")
  }

  implicit val format: Format[Channe] = new Format[Channe] {
    def reads(json: JsValue): JsResult[Channe] = apply(json)
    def writes(Channe: Channe): JsString = unapply(Channe)
  }
}
