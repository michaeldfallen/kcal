package office365

import play.api.libs.ws._
import scala.concurrent.Future
import scala.concurrent.duration._
import config.Config
import play.api.libs.json._
import play.api.cache.Cache
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import rooms._
import events.EventBrief

object Office365 {
  val baseUrl = "https://outlook.office365.com/EWS/OData"
  val userUrl = s"$baseUrl/Users('%s')/"
  val eventsUrl = s"$baseUrl/Users('%s')/Events/"
  val eventDetailsUrl = s"$baseUrl/Users('%s')/Events('%s')/"
  val bookingUrl = s"$baseUrl/Me/Events"

  def username = Config.username
  def password = Config.password

  implicit def durationToInt(d: Duration): Int = d.toSeconds.toInt

  def cacheBustRoomDetails(room: BaseRoomDetail) = Cache.remove(s"office365.roomDetails.${room.email}")
  def cacheBustEventsList(room: BaseRoomDetail) = {
    Cache.remove(s"office365.eventsList.${room.email}")
    Cache.remove(s"roomDashboard.${room.email}")
  }

  def roomDetails(room: BaseRoomDetail): Future[WSResponse] = {
    val cached = Cache.getAs[WSResponse](s"office365.roomDetails.${room.email}")
    cached.map(Future(_)).getOrElse {
      WS.url(userUrl.format(room.email))
        .withAuth(username, password, WSAuthScheme.BASIC)
        .get()
        .map { response =>
          if (response.status == 200) {
            Cache.set(s"office365.roomDetails.${room.email}", response, 1.day)
          }
          response
        }
    }
  }

  def eventsList(room: BaseRoomDetail): Future[WSResponse] = {
    val cached = Cache.getAs[WSResponse](s"office365.eventsList.${room.email}")
    cached.map(r => println(s"Fetched ${room.email} from cache"))
    cached.map(Future(_)).getOrElse {
      WS.url(eventsUrl.format(room.email))
        .withAuth(username, password, WSAuthScheme.BASIC)
        .get()
        .map { response =>
          if (response.status == 200) {
            Cache.set(s"office365.eventsList.${room.email}", response, 5.minutes)
          }
          response
        }
    }
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
    WS.url(bookingUrl)
      .withAuth(username, password, WSAuthScheme.BASIC)
      .post(json)
  }
}
