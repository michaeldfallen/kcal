package controllers

import play.api.mvc._
import play.api.cache.Cached
import play.api.Play.current
import io.michaelallen.mustache.PlayImplicits

object HomePresenter extends Controller with PlayImplicits {

  case class Home(
      allRoomsUrl: String = routes.RoomsListPresenter.roomsList("all").url,
      belfastRoomsUrl: String = routes.RoomsListPresenter.roomsList("belfast").url
  ) extends mustache.home

  def index = Cached(_ => "homePage", duration = 500) {
    Action {
      Ok(Home())
    }
  }
}
