package rooms

import play.FormDelegate
import com.github.nscala_time.time.Imports._

case class Room (
    val email: String
) extends BaseRoomDetail

trait BaseRoomDetail {
  val email: String
}

case class RoomDetails (
    val email: String,
    displayName: String,
    alias: String
) extends BaseRoomDetail {
  def shortDisplayName:String = {
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
