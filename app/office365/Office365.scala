package office365

import play.api.libs.ws.{WS, WSResponse, WSAuthScheme}
import scala.concurrent.Future
import scala.concurrent.duration._
import config.Config
import play.api.libs.json._
import play.api.Play.current
import play.api.cache.Cache
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import rooms._
import events.EventBrief

object Office365 extends Office365

trait Office365 {
  val baseUrl = "https://outlook.office365.com/EWS/OData"
  def username = Config.username
  def password = Config.password
  implicit def durationToInt(d: Duration): Int = d.toSeconds.toInt

  def buildRequest(url: String) = WS.url(url).withAuth(username, password, WSAuthScheme.BASIC)

  def userResource(email: String) = {
    buildRequest(s"$baseUrl/Users('$email')/")
  }

  def eventsResource(email: String) = {
    buildRequest(s"$baseUrl/Users('$email')/Events/")
  }

  def eventDetailsResource(email: String, id: String) = {
    buildRequest(s"$baseUrl/Users('$email')/Events('$id')/")
  }

  def bookingResource = {
    buildRequest(s"$baseUrl/Me/Events")
  }

  def roomDetails(room: BaseRoomDetail): Future[WSResponse] = {
    userResource(room.email).get()
  }

  def eventsList(room: BaseRoomDetail): Future[WSResponse] = {
    eventsResource(room.email).get()
  }

  def bookRoom(room: RoomDetails, event: EventBrief) = {
    val json = Json.obj(
      "@odata.type" -> "#Microsoft.Exchange.Services.OData.Model.Event",
      "Subject" -> event.subject,
      "Start" -> event.startIsoString,
      "End" -> event.endIsoString,
      "Location" -> Json.obj(
        "DisplayName" -> room.displayName
      ),
      "Attendees" -> Json.arr(
        Json.obj(
          "Name" -> room.displayName,
          "Address" -> room.email,
          "Type" -> "Resource"
        )
      )
    )
    bookingResource.post(json)
  }
}
