package engine

import play.Logger._
import services.ExportService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scala.language.implicitConversions
import scala.collection.JavaConverters._

import java.util.{Map => JMap}

import models.Kvit._

object ExportEngine {

  case class StartExportAttr(month: Int,
                             year: Int,
                             mkdType: String,
                             division: String,
                             premId: String,
                             codeArray: String)

  def addToGroup(listAddress: List[JMap[String, String]],
                 source: List[List[JMap[String, String]]],
                 partitionCnt: Int): List[List[JMap[String, String]]] = {
    (source,listAddress) match {
      case (s, Nil) => s
      case (Nil,l) => List(l)
      case (h :: t, l) if h.size < partitionCnt => (h ::: l) :: t
      case (h :: t, l) => l :: h :: t
    }
  }

  def attrByName(a: (JMap[String, String], Int), attrName: String) = a match {
    case (e, i) => e.get(attrName)
  }

  def index(a: Seq[(JMap[String, String], Int)]) = a match {
    case (e, i) :: _ => i
  }

  def splitByPartition(listPostal: List[(JMap[String, String], Int)],
                       partitionCnt: Int) = {
    listPostal.groupBy(attrByName(_, ADDRESS_SHORT))
      .values
      .toSeq
      .sortBy(index)
      .map(l => l.map(_._1))
      .foldLeft(List.empty[List[JMap[String, String]]])((b, a) => addToGroup(listAddress = a, source = b, partitionCnt = partitionCnt))
      .reverse
  }


  def group(kvits: Seq[(JMap[String, String], Int)], partitionCnt: Int) = {
    kvits.groupBy(attrByName(_, POSTAL))
      .values
      .toSeq
      .sortBy(index)
      .flatMap(listPostal => splitByPartition(listPostal.toList, partitionCnt = partitionCnt))
  }

  def start(attr: StartExportAttr) = {
    info(s"start: attr = $attr")
    attr.codeArray.split(";").map((code) => Future {
      //group(ExportService.run(code).asScala.zipWithIndex, partitionCnt = 1000)
    })
  }


}
