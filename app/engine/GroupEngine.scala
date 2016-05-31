package engine

import java.util.{List => JList, Map => JMap}

import models.Kvit._
import play.Logger._

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object GroupEngine {

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

  def group(bills: Seq[(JMap[String, String], Int)], partitionCnt: Int) = {
    bills.groupBy(attrByName(_, POSTAL))
      .values
      .toSeq
      .sortBy(index)
      .flatMap(listPostal => splitByPartition(listPostal.toList, partitionCnt = partitionCnt))
  }

  def run(bills: JList[JMap[String, String]]): Seq[List[JMap[String, String]]] = {
    info(s"group size = ${bills.size}")
    group(bills = bills.asScala.zipWithIndex, partitionCnt = 1000)
  }

}
