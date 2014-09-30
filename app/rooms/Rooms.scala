package rooms

import config.Config
import office365.Office365
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import org.joda.time.{DateTimeZone, DateTime}

object Rooms {
  def allRooms(): Seq[Room] = {
    val seqOfRooms = Config.roomGroups.map {
      case (key, id) => rooms(id)
    }
    seqOfRooms.flatten.toSeq
  }

  def rooms(configId: String): Seq[Room] = {
    Config.roomsList(configId).map { item =>
      Room(item)
    }
  }

  def roomDetails(room: BaseRoomDetail): Future[RoomDetails] = {
    Office365.roomDetails(room) map { response =>
      RoomDetails.bind(response.json).get
    }
  }

  def timezone(room: BaseRoomDetail): DateTimeZone = {
    Config.timezones.get(room.email).map { timezoneString =>
      DateTimeZone.forID(timezoneString)
    } getOrElse DateTimeZone.forID("Europe/London")
  }
}
