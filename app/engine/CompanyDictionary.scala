package engine

import java.util.concurrent.ConcurrentHashMap

import engine.db.DictionaryDBReader
import play.Logger._

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

object CompanyDictionary {

  private val m = new ConcurrentHashMap[String,String]().asScala

  def get(code: String):String = m.get(code) match {
    case Some(company) => company
    case None =>
      val company = DictionaryDBReader.readFromDb(code = code) match {
        case Success(company) => company
        case Failure(e) =>
          error(s"Ошибка при получения наименования компании для кода: $code; $e")
          " "
      }
      m.putIfAbsent(code,company)
      company
  }

}
