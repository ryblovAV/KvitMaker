package engine

import java.util.{Calendar, Date}
import java.util.concurrent.ConcurrentHashMap

import config.AppConfig

import scala.collection.JavaConverters._

case class ActiveCodeInfo(code:String, ip: String, dt: Date = Calendar.getInstance().getTime, processId: String) {
  def date: String = AppConfig.df.format(dt)
}

object ActiveCodeStorage {

  private val activeKeys = new ConcurrentHashMap[String,ActiveCodeInfo]().asScala

  def removeFromActiveKey(code: String, codeToKey: String => String) = {
    activeKeys.remove(codeToKey(code))
  }

  def add(codeArray: Array[String], codeToKey: String => String, ip: String, processId: String) = {
    codeArray.map(codeToKey)
      .foreach(code => activeKeys.putIfAbsent(
        code,
        ActiveCodeInfo(code = code, ip = ip, processId = processId))
      )
  }

  def check(code: String, codeToKey: String => String) = {
    activeKeys.contains(codeToKey(code))
  }

  def listActiveCode = activeKeys.values.toList.sortBy(e => e.dt.getTime)

  override def toString: String = {
    activeKeys.keys.mkString(";")
  }
}
