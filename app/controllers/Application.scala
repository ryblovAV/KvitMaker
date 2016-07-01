package controllers

import java.io.File
import java.util.Calendar

import engine._
import engine.db._
import engine.file.FileEngine
import models.StartExportAttr
import play.Logger._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json._
import play.api.libs.json._
import play.api.mvc._
import play.twirl.api.Html

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
      (__ \ 'codeArrayStr).read[String] and
      (__ \ 'orderByIndex).read[Int]
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

  def logResult(l: List[Array[Try[List[String]]]]) = {
    l.flatMap(a => a.toList).map{
      case Success(s) => s"result: $s"
      case Failure(e) => s"error: $e"
    }
  }

  def startProcess = Action(parse.json) {
    request => {
      info("------------------ start process" + Calendar.getInstance().getTime.toString + " ip: " + request.remoteAddress)
      request.body.validate[StartExportAttr].map {
        case attr: StartExportAttr =>

          val processId = Calendar.getInstance().getTimeInMillis().toString

          val codeArray = if (attr.isFindByPremiseId) DBReader.getPremiseCode(attr.mkdPremiseId) else attr.codeArray

          val filterCodeArray = checkActiveKey(codeArray, attr.key, ip = request.remoteAddress, processId = processId)

          if (!filterCodeArray.isEmpty) {
            info(s"find repeat code ${filterCodeArray.mkString(";")}")
            Ok(toJson(ExportResultInfo(processId = "", error = MessageBuilder.repeatCodeMessage(filterCodeArray))))
          } else {

              info(s"start processId = $processId; attr = $attr")

              val f = ExportEngine.start(
                mkdPremiseId = attr.mkdPremiseId,
                dt = attr.dt,
                mkdChs = attr.mkdChs,
                cisDivision = attr.cisDivision,
                codeArray = attr.codeArray,
                dbLogWriter = new DBLogWriter(processId),
                processId = processId,
                removeFromActiveKey = (code => ActiveCodeStorage.removeFromActiveKey(code, attr.key)),
                orderByIndex = attr.orderByIndex
              )

              f.onComplete(r => r match {
                case Success(s) =>
                  info(s"processId = $processId complete: ${Calendar.getInstance().getTime} ${logResult(s)}")

                  if (!s.isEmpty) {
                    ProcessResultDBStorage.addProcessResult(
                      ProcessResult(
                        processId = processId,
                        fileName = FileEngine.makeZip(processId),
                        ip = request.remoteAddress,
                        codeArray = codeArray,
                        month = attr.month,
                        mkdType = attr.mkdType,
                        mkdPremiseId = attr.mkdPremiseId
                      )
                    )
                  } else {
                    info("not found data")
                    Ok(toJson(ExportResultInfo(processId,"NO DATA FOUND")))
                  }
                case Failure(e) =>
                  error(e.toString)
                  Ok(toJson(ExportResultInfo(processId,e.getMessage)))
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

  def checkActiveKey(codeArray: Array[String], codeToKey: String => String, ip: String, processId: String) = {
    info(s"codeArray = ${codeArray.mkString(";")}")
    info(s"activeKey before = ${ActiveCodeStorage.toString}")
    val codeInActive = codeArray.filter(code => ActiveCodeStorage.check(code, codeToKey))
    if (codeInActive.isEmpty)
      ActiveCodeStorage.add(codeArray,codeToKey,ip,processId)

    info(s"codeInActive: ${codeInActive.mkString(";")}")
    info(s"activeKey after= ${ActiveCodeStorage.toString}")
    codeInActive
  }

  def index = Action {
    Ok(views.html.startExport())
  }

  def getProgress(processId: String) = Action {
    val progressArray = DBLogReader.readProgress(processId)
    Ok(toJson(progressArray))
  }

  def getArchiveFileName(processId: String) = Action {
    Ok(ProcessResultDBStorage.getFileName(processId))
  }

  def getFile(processId: String) = Action {
    ProcessResultDBStorage.getFileNameOpt(processId) match {
      case Some(fileName) =>
        info(s"processId = $processId, send file $fileName")
        Ok.sendFile(new File(fileName))
      case None =>
        info(s"processId = $processId, file not found. processId = $processId")
        NotFound
    }
  }

  def getProcesses = Action {
    Ok(views.html.listProcesses(ProcessResultDBStorage.getProcesses))
  }

  def getActives = Action {
    Ok(views.html.listActiveCode(ActiveCodeStorage.listActiveCode))
  }


}