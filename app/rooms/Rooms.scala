package rooms

import config.Config
import office365.Office365
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.joda.time.DateTime

object Rooms {
  def allRooms(): Seq[Room] = {
    belfastRooms
  }

  def belfastRooms(): Seq[Room] = {
    Config.belfastRoomsConfig.map { item =>
      Room(item)
    }
  }

  def roomDetails(room: BaseRoomDetail): Future[RoomDetails] = {
    Office365.roomDetails(room) map { response =>
      RoomDetails.bind(response.json).get
    }
  }
}
