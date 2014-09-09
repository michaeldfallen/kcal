package controllers

import play.api._
import play.api.mvc._
import play.api.cache.Cached
import play.api.Play.current
import io.michaelallen.mustache.PlayImplicits
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import rooms._
import events._
import config.Config

object RoomsListPresenter extends Controller with PlayImplicits {

  case class RoomListItem(
    room: RoomDetails,
    url: String,
    status: RoomStatus
  )

  object RoomListItem {
    def apply(room: RoomDetails, status: RoomStatus): RoomListItem = {
      RoomListItem(
        room = room,
        status = status,
        url = routes.RoomDashboardPresenter.roomDashboard(room.email).url
      )
    }
  }

  case class RoomsList(
    title: String,
    rooms: Seq[RoomListItem]
  ) extends mustache.roomsList

  object RoomsList {
    def apply(title: String, rooms: Seq[Room]): Future[RoomsList] = {
      val futureRoomList = Future.sequence(
        rooms.par.map { room =>
          val details = Rooms.roomDetails(room)
          val events = Events.todaysEvents(room)
          val status = RoomStatus(events)
          for {
            roomStatus <- status
            roomDetails <- details
          } yield {
            RoomListItem(roomDetails, roomStatus)
          }
        }.seq
      )
      futureRoomList.map { list =>
        RoomsList(title, list)
      }
    }
  }

  def roomsList(office: String) = Cached(_ => s"roomsList.$office", duration = 60) {
    Action.async {
      (office, Config.roomGroups.get(office)) match {
        case ("all", _) => {
          val roomsList = RoomsList("All Meeting Rooms", Rooms.allRooms())
          roomsList.map { rooms =>
            Ok(rooms)
          }
        }
        case (_, Some(id)) => {
          val roomsList = RoomsList(s"${office.capitalize} Meeting Rooms", Rooms.rooms(id))
          roomsList.map { rooms =>
            Ok(rooms)
          }
        }
        case _ => Future { Redirect(routes.HomePresenter.index()) }
      }
    }
  }
}
