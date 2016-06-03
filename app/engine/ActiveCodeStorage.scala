package engine

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters._

object ActiveCodeStorage {

  private val activeKeys = new ConcurrentHashMap[String,Unit]().asScala

  def removeFromActiveKey(code: String, codeToKey: String => String) = {
    activeKeys.remove(codeToKey(code))
  }

  def add(codeArray: Array[String], codeToKey: String => String) = {
    codeArray.map(codeToKey).foreach(code => activeKeys.putIfAbsent(code,()))
  }

  def check(code: String, codeToKey: String => String) = {
    activeKeys.contains(codeToKey(code))
  }

  override def toString: String = {
    activeKeys.keys.mkString(";")
  }
}
