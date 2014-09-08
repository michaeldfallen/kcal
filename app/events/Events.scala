package events

import office365.Office365
import config.Config
import rooms.BaseRoomDetail
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray

object Events {

  def allEvents(
      room: BaseRoomDetail
  ): Future[Seq[EventBrief]] = {
    Office365.eventsList(room) map { response =>
      val js = (response.json \ "value").as[JsArray]
      js.value map { EventBrief.bind(_).get }
    }
  }

  def todaysEvents(
      room: BaseRoomDetail
  ): Future[Seq[EventBrief]] = {
    allEvents(room).map { events =>
      events.filter {
        event => event.isToday
      }.sortWith {
        _.start isBefore _.start
      }
    }
  }
}
