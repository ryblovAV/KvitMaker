package engine

import java.util.concurrent.ConcurrentHashMap

import play.Logger._

import scala.collection.JavaConverters._

object ProcessResult {

  private val processResult = new ConcurrentHashMap[String,String]().asScala

  def addProcessResult(processId: String, fileName: String) = {
    info(s"put result ($processId -> $fileName)")
    processResult.putIfAbsent(processId,fileName)
  }

  def getProcessResult(processId: String) = {
    info(s"get result processId = $processId")
    processResult.get(processId)
  }

  def getFileName(processId: String) =  getProcessResult(processId) match {
    case Some(fileName) => fileName
    case None => ""
  }

}
