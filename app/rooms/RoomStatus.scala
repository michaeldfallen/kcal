package rooms

import events.{EventBriefInTimeZone, EventBrief}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.joda.time.format.PeriodFormatterBuilder
import com.github.nscala_time.time.Imports._


object PeriodFormat {
  val roughHoursMins = {
    new PeriodFormatterBuilder()
      .appendHours()
      .appendSuffix(" hr", " hrs")
      .appendSeparator("", " ")
      .appendMinutes()
      .appendSuffix(" min", " mins")
      .toFormatter
  }
}

case class AvailableOrBusyStatus (
    available: Boolean,
    currentEvent: Option[EventBriefInTimeZone],
    duration: Interval
) extends RoomStatus {

  def durationMessage = {
    val eventEndsToday = duration.end.toLocalDate.equals(LocalDate.today)
    val time = PeriodFormat.roughHoursMins.print(duration.toPeriod)
    if (available) {
      s"For $time"
    } else if (eventEndsToday) {
      s"For $time"
    } else {
      s"All day"
    }
  }
}

case class FreeAllDayStatus () extends RoomStatus {
  val available = true
  val currentEvent = None
  def durationMessage = "For the rest of the day"
}

trait RoomStatus {
  val available: Boolean
  val currentEvent: Option[EventBriefInTimeZone]
  def durationMessage: String
}

object RoomStatus {
  def apply(
      eventsList: Future[Seq[EventBriefInTimeZone]]
  ): Future[RoomStatus] = {
    eventsList map { list =>
      val sorted = list sortWith {
        _.start isBefore _.start
      } filter {
        _.isToday
      } filter {
        _.end isAfter DateTime.now
      }
      sorted.headOption map {
        case event => {
          val now = DateTime.now

          val available = event.start isAfter now
          val duration = if (available) {
            now to event.start
          } else {
            now to event.end
          }

          AvailableOrBusyStatus(available, Some(event), duration)
        }
      } getOrElse {
        FreeAllDayStatus()
      }
    }
  }
}

