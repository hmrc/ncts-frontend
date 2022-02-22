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

package models

import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue}

sealed trait DowntimeChannel

case object GBDepartures extends DowntimeChannel
case object XIDepartures extends DowntimeChannel
case object GBArrivals extends DowntimeChannel
case object XIArrivals extends DowntimeChannel
case object Web extends DowntimeChannel
case object XML extends DowntimeChannel

object DowntimeChannel {

  def apply(DowntimeChannel: JsValue): JsResult[DowntimeChannel] = DowntimeChannel.as[String] match {
    case "GB Departures" => JsSuccess(GBDepartures)
    case "XI Departures" => JsSuccess(XIDepartures)
    case "GB Arrivals" => JsSuccess(GBArrivals)
    case "XI Arrivals" => JsSuccess(XIArrivals)
    case "Web channel" => JsSuccess(Web)
    case "XML channel" => JsSuccess(XML)
    case value => JsError(s"Failed to construct DowntimeChannel from value $value")
  }

  def unapply(DowntimeChannel: DowntimeChannel): JsString = DowntimeChannel match {
    case GBDepartures => JsString("GB Departures")
    case XIDepartures => JsString("XI Departures")
    case GBArrivals => JsString("GB Arrivals")
    case XIArrivals => JsString("XI Arrivals")
    case Web => JsString("Web Channel")
    case XML => JsString("XML Channel")
  }

  implicit val format: Format[DowntimeChannel] = new Format[DowntimeChannel] {
    def reads(json: JsValue): JsResult[DowntimeChannel] = apply(json)
    def writes(DowntimeChannel: DowntimeChannel): JsString = unapply(DowntimeChannel)
  }
}