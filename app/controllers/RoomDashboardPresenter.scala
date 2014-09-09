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
    room: RoomDetails,
    eventsList: Seq[EventBrief],
    status: RoomStatus
  ) extends mustache.roomDashboard

  def roomDashboard(email: String) = Cached(_ => s"roomDashboard.$email", duration = 60) {
    Action.async {
      val room = Room(email)
      val roomDetails = Rooms.roomDetails(room)
      val eventsList = Events.todaysEvents(room)
      val roomStatus = RoomStatus(eventsList)

      for {
        details <- roomDetails
        events <- eventsList
        status <- roomStatus
      } yield {
        Ok(RoomDashboard(details, events, status))
      }
    }
  }
}
