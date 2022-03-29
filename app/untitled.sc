import models.Channel._
import models.{Channel, PlannedDowntime, PlannedDowntimeViewModel}

import java.time.{LocalDate, LocalDateTime, LocalTime}

val date = LocalDate.now
val time = LocalTime.now

val model = PlannedDowntimeViewModel(
  Some(PlannedDowntime(date, time, date, time, Channel.gbDepartures)),
  Some(PlannedDowntime(date, time, date, time, Channel.xiDepartures)),
  Some(PlannedDowntime(date, time, date, time, gbArrivals)),
  Some(PlannedDowntime(date, time, date, time, xiArrivals))
)

println(model)

model.gbArrivals.fold(
  //same as before
  "hey"
) { arrival =>
  val startDateTime = LocalDateTime.of(arrival.startDate, arrival.startTime)
  val endDateTime = LocalDateTime.of(arrival.endDate, arrival.endTime)
  val now = LocalDateTime.now()

  if ((now.isEqual(startDateTime) || now.isAfter(startDateTime))
    && (now.isEqual(endDateTime) || now.isBefore(endDateTime))) {
    // planned downtime
    ""
  } else {
    //not planned downtime
    ""
  }
}