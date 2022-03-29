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
import java.time.{LocalDate,LocalTime, LocalDateTime, ZoneId}
import java.util.Locale

object DateTimeFormatter {
  def formatDate(localDate: LocalDate): String = {
    val formatter = DateTimeGen.ofPattern("EEEE d MMMM yyyy")
    formatter.format(localDate)
  }

  def setTimeOffset(dateTime: LocalDateTime): LocalDateTime = {
    val dateTimeWithZone = dateTime.atZone(ZoneId.of("Europe/London"))
    val isGMT = dateTimeWithZone.getOffset.getTotalSeconds == 0

    if(isGMT){
      dateTime
    } else {
      dateTime.plusHours(1)
    }
  }

  def formatTimePlannedDowntime(dateTime: LocalDateTime)(implicit messages: Messages): String = {
    val timeFormat = if (dateTime.toLocalTime.getMinute > 0) {
      DateTimeGen.ofPattern("h:mma")
    } else {
      DateTimeGen.ofPattern("ha")
    }

    s"${dateTime.format(timeFormat).toLowerCase(Locale.ENGLISH)} ${getTimeZone(dateTime)}"
  }

  def formatTime(dateTime: LocalDateTime)(implicit messages: Messages): String = {

    val timeFormat = if (dateTime.toLocalTime.getMinute > 0) {
      DateTimeGen.ofPattern("h:mma")
    } else {
      DateTimeGen.ofPattern("ha")
    }

    val offsetTime = setTimeOffset(dateTime)

    s"${offsetTime.format(timeFormat).toLowerCase(Locale.ENGLISH)} ${getTimeZone(dateTime)}"
  }

  def formatTimeDowntimeHistory(dateTime: LocalDateTime)(implicit messages: Messages): String = {

    val timeFormat = if (dateTime.toLocalTime.getMinute > 0) {
      DateTimeGen.ofPattern("h:mma")
    } else {
      DateTimeGen.ofPattern("ha")
    }

    s"${dateTime.format(timeFormat).toLowerCase(Locale.ENGLISH)} ${getTimeZone(dateTime)}"
  }

  def getTimeZone(localDateTime: LocalDateTime)(implicit messages: Messages): String = {
    val dateTimeWithZone = localDateTime.atZone(ZoneId.of("Europe/London"))

    val isGMT = dateTimeWithZone.getOffset.getTotalSeconds == 0

    if(isGMT){
      messages("service.availability.issues.GMT")
    } else {
      messages("service.availability.issues.BST")
    }
  }

  def formatDateDowntimeHistory(dateTime: LocalDateTime): String =
    s"${dateTime.format(DateTimeGen.ofPattern("d MMMM yyyy"))}"

  def formatDateTime(dateTime: LocalDateTime)(implicit messages: Messages): String = {
    val knownIssueSince = dateTime.toLocalDate
    val now = LocalDate.now

    val time = formatTime(dateTime)

    if (now.isAfter(knownIssueSince)) {
      s"$time, " +
        s"${dateTime.format(DateTimeGen.ofPattern("d MMMM yyyy"))}"
    } else {
      s"$time"
    }
  }

  def createDateTime(date: LocalDate, time: LocalTime) = {
    LocalDateTime.of(date, time)
  }
}
