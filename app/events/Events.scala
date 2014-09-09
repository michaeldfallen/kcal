package events

import office365.Office365
import config.Config
import rooms.BaseRoomDetail
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsArray
import scala.util.{Failure, Success, Try}

object Events {

  def allEvents(
      room: BaseRoomDetail
  ): Future[Seq[EventBrief]] = {
    Office365.eventsList(room) map { response =>
      Try {
        val js = (response.json \ "value").as[JsArray]
        js.value map { EventBrief.bind(_).get }
      } match {
        case Success(js) => js
        case Failure(throwable) => {
          Office365.cacheBustEventsList(room)
          throw throwable
        }
      }
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
