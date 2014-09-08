package play

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.JsValue

trait FormDelegate[A] {
  val form: Form[A]

  def bind(js: JsValue) = form.bind(js)

  val playMappings = play.api.data.Forms
  val Form = play.api.data.Form
  type Mapping[B] = play.api.data.Mapping[B]
}
