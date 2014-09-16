package events

import office365.Office365
import config.Config
import rooms.{Rooms, BaseRoomDetail}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray
import scala.util.{Failure, Success, Try}
import com.github.nscala_time.time.Imports._
import play.api.cache.Cache

object Events {

  def upcomingEvents(
      room: BaseRoomDetail
  ): Future[Seq[EventBrief]] = {
    Office365.upcomingEvents(room)
      .map(EventBrief.bind)
      .map { events =>
        events.sortWith {
          _.start isBefore _.start
        }
      }
  }

  def todaysEvents(
      room: BaseRoomDetail
  ): Future[Seq[EventBrief]] = {
    Office365.upcomingEvents(room)
      .map(EventBrief.bind)
      .map { events =>
        events.filter {
          event => event.isToday
        }.sortWith {
          _.start isBefore _.start
        }
      }
  }

  def bookRoomFor10Mins(
      room: BaseRoomDetail
  ): Future[Boolean] = {
    val in10Mins = DateTime.now + 10.minutes
    val roomDetails = Rooms.roomDetails(room)
    val endTime = upcomingEvents(room).map { events =>
      events.headOption match {
        case Some(event) => {
          if (event.start isBefore in10Mins) {
            event.start
          } else {
            in10Mins
          }
        }
        case _ => in10Mins
      }
    }

    roomDetails.flatMap { room =>
      endTime.flatMap { end =>
        if (end isBefore DateTime.now) {
          Future.successful(false)
        } else {
          val event = EventBrief(
            subject = "Ad-hoc meeting",
            start = DateTime.now,
            end = end,
            importance = "",
            isAllDay = false,
            isCancelled = false
          )

          Office365.bookRoom(room, event).map {
            case response => {
              if (response.status == 200) {
                true
              } else {
                throw new Exception("Room booking failed because: " + response.body)
              }
            }
          }
        }
      }
    }
  }
}
