package events

import org.joda.time.format.{ISODateTimeFormat, PeriodPrinter, PeriodFormatterBuilder}
import com.github.nscala_time.time.Imports._
import play.FormDelegate
import org.joda.time.{Minutes, ReadablePeriod}
import java.util.Locale
import java.io.Writer

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
    } else if (end.toLocalDate.equals(LocalDate.tomorrow)) {
      end.toString(DateTimeFormat.shortTime()) + " tomorrow"
    } else {
      end.toString(DateTimeFormat.forPattern("h:m a EEEEEEEEE"))
    }
  }
  def endIsoString = {
    end.toString(ISODateTimeFormat.dateTimeNoMillis())
  }
  def startTimeString = {
    start.toString(DateTimeFormat.shortTime())
  }
  def startIsoString = {
    start.toString(ISODateTimeFormat.dateTimeNoMillis())
  }
  def durationString = {
    val duration = (start to end).toPeriod
    if (duration.toStandardMinutes.isLessThan(5.minutes.toPeriod.toStandardMinutes)) {
      "a few minutes"
    } else {
      PeriodFormat.roughHoursMins.print(duration)
    }
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
