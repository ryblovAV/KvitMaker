package controllers

import com.google.inject.Inject
import play.api.db.Database
import play.api.mvc._
import services.ExportService

class Application @Inject() (db: Database) extends Controller {

  def startUnload = Action {
    val message = "OK";// ExportService.run("11")
    Ok(message)
  }

}