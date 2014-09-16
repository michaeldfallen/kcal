package events

import org.scalatest._
import com.github.nscala_time.time.Imports._

class EventBriefTests extends FlatSpec with Matchers {

  def eventThatEndsIn(duration: Duration) = {
    EventBrief("A meeting", DateTime.now, DateTime.now + duration, "", false, false)
  }

  "EventBrief.durationString" should "print rough number of hours" in {
    eventThatEndsIn(1.hour).durationString should be("1 hr")
  }

  it should "a few mins for less than 5 mins long" in {
    eventThatEndsIn(1.minute).durationString should be("a few minutes")
  }

  it should "X mins for greater than 5 mins long" in {
    eventThatEndsIn(6.minute).durationString should be("6 mins")
    eventThatEndsIn(16.minute).durationString should be("16 mins")
    eventThatEndsIn(26.minute).durationString should be("26 mins")
    eventThatEndsIn(36.minute).durationString should be("36 mins")
    eventThatEndsIn(46.minute).durationString should be("46 mins")
    eventThatEndsIn(56.minute).durationString should be("56 mins")
  }

  it should "list mins and hrs" in {
    eventThatEndsIn(1.hour + 30.minutes).durationString should be("1 hr 30 mins")
  }

  "EventBrief.endIsoString" should "print proper ISO datetime" in {
    val date = new DateTime(1, 2, 3, 4, 5, 6, 7, DateTimeZone.UTC)
    EventBrief("A meeting", DateTime.now, date, "", false, false).endIsoString should be("0001-02-03T04:05:06Z")
  }

  "EventBrief.startIsoString" should "print proper ISO datetime" in {
    val date = new DateTime(1, 2, 3, 4, 5, 6, 7, DateTimeZone.UTC)
    EventBrief("A meeting", date, DateTime.now, "", false, false).startIsoString should be("0001-02-03T04:05:06Z")
  }
}
