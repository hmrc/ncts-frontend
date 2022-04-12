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

import play.api.i18n.Messages
import play.api.libs.json.{Format, JsError, JsResult, JsString, JsSuccess, JsValue}

sealed trait Channel {
  private val mesgKey = Channel.unapply(this).value.replace(' ', '.').toLowerCase()

  def caption(implicit messages: Messages): String = {
    messages(s"service.availability.timeline.channel.caption.$mesgKey")
  }

  def displayName(implicit messages: Messages): String = {
    messages(s"service.availability.timeline.channel.display.$mesgKey")
  }

  def isPPN: Boolean = false
  def isGbOrXi: Boolean = false
}

case object GBDepartures extends Channel {
  override def isGbOrXi: Boolean = true
}
case object XIDepartures extends Channel {
  override def isGbOrXi: Boolean = true
}
case object GBArrivals extends Channel {
  override def isGbOrXi: Boolean = true
}
case object XIArrivals extends Channel {
  override def isGbOrXi: Boolean = true
}
case object Web extends Channel
case object XML extends Channel
case object PPN extends Channel {
  override def isPPN: Boolean = true
}

object Channel {

  def apply(Channel: JsValue): JsResult[Channel] = Channel.as[String] match {
    case "GB Departures" => JsSuccess(GBDepartures)
    case "XI Departures" => JsSuccess(XIDepartures)
    case "GB Arrivals" => JsSuccess(GBArrivals)
    case "XI Arrivals" => JsSuccess(XIArrivals)
    case "Web channel" => JsSuccess(Web)
    case "XML channel" => JsSuccess(XML)
    case "PPN" => JsSuccess(PPN)
    case value => JsError(s"Failed to construct Channel from value $value")
  }

  def unapply(Channel: Channel): JsString = Channel match {
    case GBDepartures => JsString("GB Departures")
    case XIDepartures => JsString("XI Departures")
    case GBArrivals => JsString("GB Arrivals")
    case XIArrivals => JsString("XI Arrivals")
    case Web => JsString("Web channel")
    case XML => JsString("XML channel")
    case PPN => JsString("PPN")
  }

  implicit val format: Format[Channel] = new Format[Channel] {
    def reads(json: JsValue): JsResult[Channel] = apply(json)
    def writes(Channel: Channel): JsString = unapply(Channel)
  }
}