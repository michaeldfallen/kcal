package controllers

import org.joda.time.DateTimeZone
import play.api._
import play.api.mvc._
import play.api.cache.Cached
import play.api.Play.current
import io.michaelallen.mustache.PlayImplicits
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import rooms._
import events._

object RoomDashboardPresenter extends Controller with PlayImplicits {

  case class RoomDashboard(
    room: BaseRoomDetail,
    eventsList: Seq[EventBriefInTimeZone],
    status: RoomStatus,
    bookUrl: String,
    message: String,
    exception: String
  ) extends mustache.roomDashboard

  def roomDashboard(email: String) = Action.async { request =>
    val room = Room(email)
    val timezone = Rooms.timezone(room)
    val roomDetails = Rooms.roomDetails(room)
    val eventsList = Events.todaysEvents(room)
    val eventsWithTimezone = eventsList map {
      _ map { event =>
        EventBriefInTimeZone(event, timezone)
      }
    }
    val roomStatus = RoomStatus(eventsWithTimezone)

    for {
      details <- roomDetails
      events <- eventsWithTimezone
      status <- roomStatus
    } yield {
      val displayEvents = if (status.available) {
        events
      } else {
        events.tail
      }
      Ok(RoomDashboard(
        room = details,
        eventsList = displayEvents,
        status = status,
        bookUrl = routes.EventsController.bookRoom(details.email).url,
        message = request.flash.get("message") getOrElse "",
        exception = request.flash.get("exception") getOrElse ""
      ))
    }
  }
}
