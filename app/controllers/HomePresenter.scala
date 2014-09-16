package controllers

import play.api.mvc._
import play.api.cache.Cached
import play.api.Play.current
import io.michaelallen.mustache.PlayImplicits
import config.Config

object HomePresenter extends Controller with PlayImplicits {

  case class RoomUrl(name: String, url: String)

  val urls = Config.roomGroups.map {
    case (key, _) => RoomUrl(s"${key.capitalize} Meeting Rooms", routes.RoomsListPresenter.roomsList(key).url)
  }.toSeq.sortBy(_.name)

  case class Home(
      allRoomsUrl: String = routes.RoomsListPresenter.roomsList("all").url,
      urls: Seq[RoomUrl] = urls
  ) extends mustache.home

  def index = Action {
    Ok(Home())
  }
}
