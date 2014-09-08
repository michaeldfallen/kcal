package rooms

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.JsValue

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
) extends BaseRoomDetail

object RoomDetails {
  val form = Form(
    mapping(
      "Id"          -> text,
      "DisplayName" -> text,
      "Alias"       -> text
    ) (RoomDetails.apply) (RoomDetails.unapply)
  )

  def bind(js: JsValue) = form.bind(js)
}
