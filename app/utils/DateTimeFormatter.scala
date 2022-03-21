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

package utils

import play.api.i18n.Messages

import java.time.format.{DateTimeFormatter => DateTimeGen}
import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.util.Locale

object DateTimeFormatter {
  def formatDate(localDate: LocalDate): String = {
    val formatter = DateTimeGen.ofPattern("EEEE d MMMM yyyy")
    formatter.format(localDate)
  }

  def formatTime(localTime: LocalTime): String = {
    val formatter = if (localTime.getMinute > 0) {
      DateTimeGen.ofPattern("h:mma")
    } else {
      DateTimeGen.ofPattern("ha")
    }

    formatter.format(localTime)
  }

  def formatDateTime(dateTime: LocalDateTime)(implicit messages: Messages): String =
    s"${dateTime.format(DateTimeGen.ofPattern("h:mma")).toLowerCase(Locale.ENGLISH)} ${messages("service.availability.issues.GMT")}"

  def formatDateTimeDowntimeHistory(dateTime: LocalDateTime): String =
    s"${dateTime.format(DateTimeGen.ofPattern("d MMMM yyyy"))}"

  def formatDateTimeKnownIssues(dateTime: LocalDateTime)(implicit messages: Messages): String = {
    val knownIssueSince = dateTime.toLocalDate
    val now = LocalDate.now
    if (now.isAfter(knownIssueSince))
      s"${dateTime.format(DateTimeGen.ofPattern("h:mma")).toLowerCase(Locale.ENGLISH)} ${messages("service.availability.issues.GMT")}, " +
        s"${dateTime.format(DateTimeGen.ofPattern("d MMMM yyyy"))}"
    else {
      s"${dateTime.format(DateTimeGen.ofPattern("h:mma")).toLowerCase(Locale.ENGLISH)} ${messages("service.availability.issues.GMT")}"
    }
  }
}
