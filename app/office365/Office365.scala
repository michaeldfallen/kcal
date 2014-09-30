package office365

import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import play.api.libs.ws.{WS, WSResponse, WSAuthScheme}
import scala.concurrent.Future
import scala.concurrent.duration.Duration
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
  def now = DateTime.now(DateTimeZone.UTC) .toString(ISODateTimeFormat.dateTimeNoMillis)
  def midnight = {
    DateTime
      .now(DateTimeZone.UTC)
      .withHourOfDay(23)
      .withMinuteOfHour(59)
      .withSecondOfMinute(59)
      .toString(ISODateTimeFormat.dateTimeNoMillis())
  }

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
    userResource(room.email)
      .withQueryString("$select" -> "DisplayName,Alias")
      .get()
  }

  def eventsList(room: BaseRoomDetail): Future[WSResponse] = {
    eventsResource(room.email).get()
  }

  def upcomingEvents(room: BaseRoomDetail): Future[WSResponse] = {
    eventsResource(room.email)
      .withQueryString(
        "$filter" -> s"Start lt $midnight and End gt $now",
        "$top" -> "20",
        "$select" -> "Subject,Start,End,Importance,IsAllDay,IsCancelled")
      .get()
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
