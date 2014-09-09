package config

import scala.collection.JavaConversions._
import play.api.libs.Crypto

object Config {
  private lazy val configuration = play.Play.application.configuration

  lazy val belfastRoomsList = rooms("kcal.rooms.belfast")
  lazy val londonRoomsList = rooms("kcal.rooms.london")
  lazy val gdanskRoomsList = rooms("kcal.rooms.gdansk")
  lazy val derryRoomsList = rooms("kcal.rooms.derry")
  lazy val bristolRoomsList = rooms("kcal.rooms.bristol")
  lazy val dublinRoomsList = rooms("kcal.rooms.dublin")

  def rooms(key:String): Seq[String] = {
    configuration
      .getList(key)
      .toSeq
      .map( obj => obj.toString )
  }

  def password: String = {
    Crypto.decryptAES(configuration.getString("kcal.creds.password"))
  }

  def username: String = {
    Crypto.decryptAES(configuration.getString("kcal.creds.username"))
  }
}
