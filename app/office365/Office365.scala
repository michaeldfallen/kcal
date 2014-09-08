package office365

import play.api.libs.ws._
import scala.concurrent.Future
import config.Config
import play.api.Play.current
import rooms.BaseRoomDetail

object Office365 {
  val baseUrl = "https://outlook.office365.com/EWS/OData/Users('%s')"
  val userUrl = s"$baseUrl/"
  val eventUrl = s"$baseUrl/Events"

  def username = Config.username
  def password = Config.password

  def roomDetails(room: BaseRoomDetail): Future[WSResponse] = {
    WS.url(userUrl.format(room.email))
      .withAuth(username, password, WSAuthScheme.BASIC)
      .get()
  }
}
