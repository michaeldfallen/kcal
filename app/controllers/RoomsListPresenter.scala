package controllers

import play.api._
import play.api.mvc._
import io.michaelallen.mustache.PlayImplicits
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import rooms._

object RoomsListPresenter extends Controller with PlayImplicits {

  case class RoomWithUrl(
    email: String,
    url: String
  )
  
  object RoomWithUrl {
    def apply(room: Room):RoomWithUrl = {
      RoomWithUrl(
        email = room.email,
        url = routes.RoomDashboardPresenter.roomDashboard(room.email).url
      )
    }
  }

  case class RoomsList(
    rooms: Seq[RoomWithUrl] = Rooms.allRooms map { RoomWithUrl(_) }
  ) extends mustache.roomsList

  def roomsList = Action {
    Ok(RoomsList())
  }

  def roomsListFiltered(office: String) = Action {
    office match {
      case "belfast" => Ok(RoomsList(Rooms.belfastRooms map { RoomWithUrl(_) }))
      case _ => Redirect(routes.RoomsListPresenter.roomsList)
    }
  }
}
