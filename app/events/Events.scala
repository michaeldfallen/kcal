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

  def allEvents(
      room: BaseRoomDetail
  ): Future[Seq[EventBrief]] = {
    Office365.eventsList(room) map { response =>
      Try {
        val js = (response.json \ "value").as[JsArray]
        js.value map { EventBrief.bind(_).get }
      } match {
        case Success(js) => js
        case Failure(throwable) => {
          Office365.cacheBustEventsList(room)
          Seq.empty[EventBrief]
        }
      }
    }
  }

  def upcomingEvents(
      room: BaseRoomDetail
  ): Future[Seq[EventBrief]] = {
    todaysEvents(room).map {events =>
      events.filter(_.end isAfter DateTime.now)
    }
  }

  def todaysEvents(
      room: BaseRoomDetail
  ): Future[Seq[EventBrief]] = {
    allEvents(room).map { events =>
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
      println("In upcoming events:")
      println(s"Target end time - ${in10Mins.toString()}")
      events.foreach( event =>
        println(s"${event.subject} ${event.startTimeString}")
      )
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
                Office365.cacheBustEventsList(room)
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
