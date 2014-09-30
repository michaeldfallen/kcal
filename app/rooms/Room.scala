package rooms

import config.Config
import play.FormDelegate
import com.github.nscala_time.time.Imports._

case class Room (
    email: String
) extends BaseRoomDetail {
  def name = {
    email.split('@').head
  }
}

trait BaseRoomDetail {
  val email: String
  def name: String
}

case class RoomDetails (
    email: String,
    displayName: String,
    alias: String
) extends BaseRoomDetail {
  def name:String = {
    displayName
      .replaceAllLiterally("Belfast", "")
      .replaceAllLiterally("London", "")
  }
}

object RoomDetails extends FormDelegate[RoomDetails] {
  import playMappings._

  val form = Form(
    mapping(
      "Id"          -> text,
      "DisplayName" -> text,
      "Alias"       -> text
    ) (RoomDetails.apply) (RoomDetails.unapply)
  )
}
