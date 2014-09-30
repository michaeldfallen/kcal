package config

import scala.collection.JavaConversions._
import play.api.libs.Crypto

object Config {
  private lazy val configuration = play.Play.application.configuration

  def roomsList(key:String): Seq[String] = {
    configuration
      .getList(key)
      .toSeq
      .map( obj => obj.toString )
  }

  lazy val roomGroups: Map[String, String] = {
    configuration.getConfig("kcal.rooms").subKeys().map { key => key -> s"kcal.rooms.$key.rooms" }.toMap
  }

  lazy val timezones: Map[String, String] = {
    configuration.getConfig("kcal.rooms").subKeys().map { key =>
      val rooms = roomsList(s"kcal.rooms.$key.rooms")
      val timezone = configuration.getString(s"kcal.rooms.$key.timezone")
      rooms.map { roomEmail =>
        roomEmail -> timezone
      }.toMap
    }.flatten.toMap.filter { case (key, value) => value != "" }
  }

  def password: String = {
    Crypto.decryptAES(configuration.getString("kcal.creds.password"))
  }

  def username: String = {
    Crypto.decryptAES(configuration.getString("kcal.creds.username"))
  }
}
