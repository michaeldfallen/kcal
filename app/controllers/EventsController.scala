package controllers

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import events.Events
import rooms.Room

object EventsController extends Controller {

  def bookRoom(email:String) = Action.async {
    val redirect = Redirect(routes.RoomDashboardPresenter.roomDashboard(email))
    Events.bookRoomFor10Mins(Room(email)) map {
      case true => redirect.flashing("message" -> "Booked for 10 minutes")
      case false => redirect.flashing("message" -> "Unable to book meeting")
    } recover {
      case exception => redirect.flashing(
        "message" -> "Unable to book meeting",
        "exception" -> exception.getMessage
      )
    }
  }
}
