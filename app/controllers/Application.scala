package controllers

import com.google.inject.Inject
import engine.ExportEngine
import engine.ExportEngine.StartExportAttr
import play.api.db.Database
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html
import play.Logger._

class Application @Inject() (db: Database) extends Controller {

  implicit val rds: Reads[StartExportAttr] = (
    (__ \ 'month).read[Int] and
    (__ \ 'year).read[Int] and
    (__ \ 'mkdType).read[String] and
    (__ \ 'division).read[String] and
    (__ \ 'premId).read[String] and
    (__ \ 'codeArray).read[String]
  )(StartExportAttr.apply _)

  def startUnload = Action(parse.json) {
    //val message = ExportService.run("11")
    request => {
      request.body.validate[StartExportAttr].map {
        case attr : StartExportAttr =>
          ExportEngine.start(attr)
          Ok(s"$attr")
      }.recoverTotal {
        e =>
          BadRequest(Html("<p>" + e + "</p>"))
      }
    }
  }

  def index = Action {
    Ok(views.html.export())
  }

}