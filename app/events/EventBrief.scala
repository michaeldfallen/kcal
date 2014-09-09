package events

import org.joda.time.format.PeriodFormat
import com.github.nscala_time.time.Imports._
import play.FormDelegate

case class EventBrief (
    subject: String,
    start: DateTime,
    end: DateTime,
    importance: String,
    isAllDay: Boolean,
    isCancelled: Boolean
) {
  def endTimeString = {
    if (end.toLocalDate.equals(LocalDate.today)) {
      end.toString(DateTimeFormat.shortTime())
    } else {
      end.toString(DateTimeFormat.shortDateTime())
    }
  }
  def startTimeString = {
    start.toString(DateTimeFormat.shortTime())
  }
  def durationString = {
    val duration = (start to end).toPeriod
    PeriodFormat.getDefault().print(duration)
  }

  def isToday = {
    start.toLocalDate.equals(LocalDate.today)
  }

  def isPassed = {
    end isBefore DateTime.now
  }
}

object EventBrief extends FormDelegate[EventBrief] {
  import playMappings._

  val dateTime: Mapping[DateTime] = {
    text.transform(_.toDateTime, _.toString)
  }

  val form = Form(
    mapping(
      "Subject"     -> text,
      "Start"       -> dateTime,
      "End"         -> dateTime,
      "Importance"  -> text,
      "IsAllDay"    -> boolean,
      "IsCancelled" -> boolean
    ) (EventBrief.apply) (EventBrief.unapply)
  )
}
