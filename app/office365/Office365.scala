package office365

import play.api.libs.ws._
import scala.concurrent.Future
import config.Config
import play.api.Play.current
import rooms.BaseRoomDetail

object Office365 {
  val baseUrl = "https://outlook.office365.com/EWS/OData"
  val userUrl = s"$baseUrl/Users('%s')/"
  val eventsUrl = s"$baseUrl/Users('%s')/Events/"
  val eventDetailsUrl = s"$baseUrl/Users('%s')/Events('%s')/"

  def username = Config.username
  def password = Config.password

  def roomDetails(room: BaseRoomDetail): Future[WSResponse] = {
    WS.url(userUrl.format(room.email))
      .withAuth(username, password, WSAuthScheme.BASIC)
      .get()
  }

  def eventsList(room: BaseRoomDetail): Future[WSResponse] = {
    WS.url(eventsUrl.format(room.email))
      .withAuth(username, password, WSAuthScheme.BASIC)
      .get()
  }
}
