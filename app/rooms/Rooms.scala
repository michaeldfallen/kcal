package rooms

import config.Config
import office365.Office365
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.joda.time.DateTime

object Rooms {
  def allRooms(): Seq[Room] = {
    belfastRooms() ++ londonRooms() ++ gdanskRooms() ++ derryRooms() ++ bristolRooms()
  }

  def belfastRooms(): Seq[Room] = {
    Config.belfastRoomsList.map { item =>
      Room(item)
    }
  }

  def londonRooms(): Seq[Room] = {
    Config.londonRoomsList.map { item =>
      Room(item)
    }
  }

  def gdanskRooms(): Seq[Room] = {
    Config.gdanskRoomsList.map { item =>
      Room(item)
    }
  }

  def derryRooms(): Seq[Room] = {
    Config.derryRoomsList.map { item =>
      Room(item)
    }
  }

  def bristolRooms(): Seq[Room] = {
    Config.bristolRoomsList.map { item =>
      Room(item)
    }
  }

  def roomDetails(room: BaseRoomDetail): Future[RoomDetails] = {
    Office365.roomDetails(room) map { response =>
      RoomDetails.bind(response.json).get
    }
  }
}
