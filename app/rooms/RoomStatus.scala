package rooms

import events.EventBrief
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.joda.time.format.PeriodFormat
import com.github.nscala_time.time.Imports._

case class AvailableOrBusyStatus (
    available: Boolean,
    currentEvent: Option[EventBrief],
    duration: Interval
) extends RoomStatus {

  def durationMessage = {
    val printablePeriod = duration.toPeriod.withSeconds(0).withMillis(0)
    val time = PeriodFormat.getDefault().print(printablePeriod)
    if (available) {
      s"Free for $time"
    } else {
      s"Busy for $time"
    }
  }
}

case class FreeAllDayStatus () extends RoomStatus {
  val available = true
  val currentEvent = None
  def durationMessage = "Free all day"
}

trait RoomStatus {
  val available: Boolean
  val currentEvent: Option[EventBrief]
  def durationMessage: String
}

object RoomStatus {
  def apply(
      eventsList: Future[Seq[EventBrief]]
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
        case event @ EventBrief(_, start, end, _, _, _) => {
          val now = DateTime.now

          val available = start isAfter now
          val duration = if (available) {
            now to start
          } else {
            now to end
          }

          AvailableOrBusyStatus(available, Some(event), duration)
        }
      } getOrElse {
        FreeAllDayStatus()
      }
    }
  }
}

