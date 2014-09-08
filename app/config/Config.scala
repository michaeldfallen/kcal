package config

import scala.collection.JavaConversions._
import play.api.libs.Crypto

object Config {
  private lazy val configuration = play.Play.application.configuration

  def belfastRoomsConfig: Seq[String] = {
    configuration
      .getList("kcal.rooms.belfast")
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
