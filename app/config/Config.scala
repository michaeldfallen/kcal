package config

import scala.collection.JavaConversions._
import play.api.libs.Crypto

object Config {
  private lazy val configuration = play.Play.application.configuration

  lazy val belfastRoomsList = roomsList("kcal.rooms.belfast")
  lazy val londonRoomsList = roomsList("kcal.rooms.london")
  lazy val gdanskRoomsList = roomsList("kcal.rooms.gdansk")
  lazy val derryRoomsList = roomsList("kcal.rooms.derry")
  lazy val bristolRoomsList = roomsList("kcal.rooms.bristol")
  lazy val dublinRoomsList = roomsList("kcal.rooms.dublin")

  def roomsList(key:String): Seq[String] = {
    configuration
      .getList(key)
      .toSeq
      .map( obj => obj.toString )
  }

  lazy val roomGroups: Map[String, String] = {
    configuration.getConfig("kcal.rooms").keys().map { key => key -> s"kcal.rooms.$key" }.toMap
  }

  def password: String = {
    Crypto.decryptAES(configuration.getString("kcal.creds.password"))
  }

  def username: String = {
    Crypto.decryptAES(configuration.getString("kcal.creds.username"))
  }
}
