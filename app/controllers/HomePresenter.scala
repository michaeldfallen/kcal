package controllers

import play.api.mvc._
import io.michaelallen.mustache.PlayImplicits

object HomePresenter extends Controller with PlayImplicits {

  case class Home(
      allRoomsUrl: String = routes.RoomsListPresenter.roomsList("all").url,
      belfastRoomsUrl: String = routes.RoomsListPresenter.roomsList("belfast").url
  ) extends mustache.home

  def index = Action {
    Ok(Home())
  }
}
