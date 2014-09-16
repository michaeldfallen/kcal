package controllers

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
    eventsList: Seq[EventBrief],
    status: RoomStatus,
    bookUrl: String,
    message: String,
    exception: String
  ) extends mustache.roomDashboard

  def roomDashboard(email: String) = Cached(_ => s"roomDashboard.$email", duration = 60) {
    Action.async { request =>
      val room = Room(email)
      val roomDetails = Rooms.roomDetails(room)
      val eventsList = Events.upcomingEvents(room)
      val roomStatus = RoomStatus(eventsList)

      for {
        details <- roomDetails
        events <- eventsList
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
}
