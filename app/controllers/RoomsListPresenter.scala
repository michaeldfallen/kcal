package controllers

import play.api._
import play.api.mvc._
import io.michaelallen.mustache.PlayImplicits
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import rooms._
import events._

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
    rooms: Seq[RoomListItem]
  ) extends mustache.roomsList

  object RoomsList {
    def apply(rooms: Seq[Room]): Future[RoomsList] = {
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
        RoomsList(list)
      }
    }
  }

  def roomsList = Action.async {
    val roomsList = RoomsList(Rooms.allRooms())
    roomsList.map { rooms =>
      Ok(rooms)
    }
  }

  def roomsList(office: String) = Action.async {
    office match {
      case "belfast" => {
        val roomsList = RoomsList(Rooms.belfastRooms())
        roomsList.map { rooms =>
          Ok(rooms)
        }
      }
      case "all" => {
        val roomsList = RoomsList(Rooms.allRooms())
        roomsList.map { rooms =>
          Ok(rooms)
        }
      }
      case _ => Future { Redirect(routes.HomePresenter.index()) }
    }
  }
}
