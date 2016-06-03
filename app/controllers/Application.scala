package controllers

import java.io.File
import java.util.Calendar
import java.util.concurrent.ConcurrentHashMap

import engine.db.{DBLogReader, DBLogWriter, ProgressInfo}
import engine.file.FileEngine
import engine.{ExportEngine, MessageBuilder, ProcessResult}
import models.StartExportAttr
import play.Logger._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

import scala.collection._
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success, Try}


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
      "dtStart" -> progress.dtStart,
      "dt" -> progress.dt,
      "message" -> progress.message
    )
  }

  implicit val exportResultWriters = new Writes[ExportResultInfo] {
    def writes(info: ExportResultInfo) = Json.obj(
      "processId" -> info.processId,
      "error" -> info.error)
  }

  val activeKey = mutable.Set.empty[String]

  def logResult(l: List[Array[Try[List[String]]]]) = {
    l.flatMap(a => a.toList).map{
      case Success(s) => s"result: $s"
      case Failure(e) => s"error: $e"
    }
  }

  def startProcess = Action(parse.json) {
    request => {
      info("------------------ start process" + Calendar.getInstance().getTime.toString)
      request.body.validate[StartExportAttr].map {
        case attr: StartExportAttr =>
          val filterCodeArray = checkActiveKey(attr.codeArray, attr.key)

          if (!filterCodeArray.isEmpty) {
            info(s"find repeat code ${filterCodeArray.mkString(";")}")
            Ok(toJson(ExportResultInfo(processId = "", error = MessageBuilder.repeatCodeMessage(filterCodeArray))))
          } else {
            val processId = Calendar.getInstance().getTimeInMillis().toString

              info(s"start processId = $processId")

              val f = ExportEngine.start(
                mkdPremiseId = attr.premId,
                dt = attr.dt,
                mkdChs = attr.mkdChs,
                cisDivision = attr.cisDivision,
                codeArray = attr.codeArray,
                dbLogWriter = new DBLogWriter(processId),
                processId = processId
              )

              f.onComplete(r => r match {
                case Success(s) =>
                  info(s"processId = $processId complete: ${Calendar.getInstance().getTime} ${logResult(s)}")
                  ProcessResult.addProcessResult(processId,FileEngine.makeZip(processId))
                  removeFromActiveKey(attr.codeArray, attr.key)
                case Failure(e) =>
                  error(e.toString)
                  Ok(toJson(ExportResultInfo(processId,e.getMessage)))
                  removeFromActiveKey(attr.codeArray, attr.key)
              })

              info("Export: Ok")
              Ok(toJson(ExportResultInfo(processId,"")))

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
      activeKey ++= codeArray.map(code => codeToKey(code))
    info(s"codeInActive: ${codeInActive.mkString(";")}")
    info(s"activeKey after= $activeKey")
    codeInActive
  }

  def removeFromActiveKey(codeArray: Array[String], codeToKey: String => String) = activeKey.synchronized {
    activeKey --= codeArray.map(code => codeToKey(code))
  }


  def index = Action {
    Ok(views.html.startExport())
  }

  def getProgress(processId: String) = Action {
    val progressArray = DBLogReader.readProgress(processId)
    Ok(toJson(progressArray))
  }

  def getArchiveFileName(processId: String) = Action {
    Ok(ProcessResult.getFileName(processId))
  }

  def getFile(processId: String) = Action {
    ProcessResult.getProcessResult(processId) match {
      case Some(fileName) =>
        info(s"send file $fileName")
        Ok.sendFile(new File(fileName))
      case None =>
        info(s"file not found. processId = $processId")
        Ok
    }
  }

}