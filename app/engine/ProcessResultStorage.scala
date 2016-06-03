package engine

import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.{Calendar, Date}

import config.AppConfig
import play.Logger._

import scala.collection.JavaConverters._

case class ProcessResult(processId: String,
                         fileName: String,
                         ip:String,
                         dt:Date = Calendar.getInstance().getTime,
                         codeArray: Array[String]) {
  def date: String = AppConfig.df.format(dt)
}

object ProcessResultStorage {

  private val processResult = new ConcurrentHashMap[String,ProcessResult]().asScala

  def addProcessResult(processId: String, result: ProcessResult) = {
    processResult.putIfAbsent(processId,result)
  }

  def getFileNameOpt(processId: String):Option[String] = {
    processResult.get(processId).map(_.fileName)
  }

  def getFileName(processId: String) =  getFileNameOpt(processId) match {
    case Some(fileName) => fileName
    case None => ""
  }

  def getProcesses: Seq[ProcessResult] = processResult.values.toList.sortBy(e => e.dt.getTime)

}
