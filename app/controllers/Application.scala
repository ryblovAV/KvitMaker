package controllers

import engine.db.{DBLogReader, DBLogWriter, ProgressInfo}
import engine.{ExportEngine, MessageBuilder}
import models.StartExportAttr
import play.Logger._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html


class Application extends Controller {

  case class ExportResultInfo(processId: String, error: String)

  implicit val rds: Reads[StartExportAttr] = (
    (__ \ 'month).read[Int] and
      (__ \ 'year).read[Int] and
      (__ \ 'mkdType).read[String] and
      (__ \ 'division).read[String] and
      (__ \ 'premId).read[String] and
      (__ \ 'codeArrayStr).read[String]
    ) (StartExportAttr.apply _)

  implicit val progressWriters = new Writes[ProgressInfo] {
    def writes(progress: ProgressInfo) = Json.obj(
      "code" -> progress.code,
      "dt" -> progress.dt,
      "message" -> progress.message
    )
  }

  implicit val exportResultWriters = new Writes[ExportResultInfo] {
    def writes(info: ExportResultInfo) = Json.obj(
      "processId" -> info.processId,
      "error" -> info.error)
  }

  var activeKey = Set.empty[String]

  def startProcess = Action(parse.json) {
    request => {
      info("------------------ start process")
      request.body.validate[StartExportAttr].map {
        case attr: StartExportAttr =>
          val filterCodeArray = checkActiveKey(attr.codeArray, attr.key)

          if (!filterCodeArray.isEmpty) {
            info(s"find repeat code ${filterCodeArray.mkString(";")}")
            Ok(toJson(ExportResultInfo(processId = "", error = MessageBuilder.repeatCodeMessage(filterCodeArray))))
          } else {
            val processId = java.util.UUID.randomUUID.toString
            try {
              info(s"start processId = $processId")

              ExportEngine.start(
                mkdPremiseId = attr.premId,
                dt = attr.dt,
                mkdChs = attr.mkdChs,
                cisDivision = attr.cisDivision,
                codeArray = attr.codeArray,
                dbLogWriter = new DBLogWriter(processId),
                processId = processId
              )
                info("Export: Ok")
                Ok(toJson(ExportResultInfo(processId,"")))
            } catch {
              case e:Exception =>
                error(e.toString)
                Ok(toJson(ExportResultInfo(processId,e.getMessage)))
            } finally {
              removeFromActiveKey(attr.codeArray, attr.key)
            }
          }
      }.recoverTotal {
        e =>
          info("json validation error: " + request.body)
          BadRequest(Html("<p>" + e + "</p>"))
      }
    }
  }

  def checkActiveKey(codeArray: Array[String], codeToKey: String => String) = activeKey.synchronized {
    info(s"codeArray = ${codeArray.mkString(";")}")
    info(s"activeKey before = $activeKey")
    val codeInActive = codeArray.filter(code => activeKey.contains(codeToKey(code)))
    if (codeInActive.isEmpty)
      activeKey = activeKey ++ codeArray.map(code => codeToKey(code))
    info(s"codeInActive: ${codeInActive.mkString(";")}")
    info(s"activeKey after= $activeKey")
    codeInActive
  }

  def removeFromActiveKey(codeArray: Array[String], codeToKey: String => String) = activeKey.synchronized {
    activeKey = activeKey -- codeArray.map(code => codeToKey(code))
  }

  def index = Action {
    Ok(views.html.startExport())
  }

  def getProgress(processId: String) = Action {
    val progressArray = DBLogReader.readProgress(processId)
    Ok(toJson(progressArray))
  }

}