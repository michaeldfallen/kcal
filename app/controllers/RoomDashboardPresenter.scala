package controllers

import play.api._
import play.api.mvc._
import io.michaelallen.mustache.PlayImplicits
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import rooms._

object RoomDashboardPresenter extends Controller with PlayImplicits {

  case class RoomDashboard(
    room: RoomDetails
  ) extends mustache.roomDashboard

  def roomDashboard(email: String) = Action.async {
    val room = Room(email)
    val roomDetails = Rooms.roomDetails(room)

    roomDetails map { details =>
      Ok(RoomDashboard(details))
    }
  }
}
