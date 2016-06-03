package engine

import java.util.{List => JList, ArrayList => JArrayList}

import config.AppConfig
import models.Kvit._
import play.Logger._

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object GroupEngine {

  def addToGroup(listAddress: List[JArrayList[String]],
                 source: List[List[JArrayList[String]]],
                 partitionCnt: Int): List[List[JArrayList[String]]] = {
    (source,listAddress) match {
      case (s, Nil) => s
      case (Nil,l) => List(l)
      case (h :: t, l) if h.size < partitionCnt => (h ::: l) :: t
      case (h :: t, l) => l :: h :: t
    }
  }

  def attrByIndex(a: (JArrayList[String], Int), attrIndex: Int) = a match {
    case (e, i) => e.get(attrIndex)
  }

  def index(a: Seq[(JArrayList[String], Int)]) = a.head match {
      case (_,i) => i
  }

  def splitByPartition(listPostal: List[(JArrayList[String], Int)],
                       partitionCnt: Int) = {
    listPostal.
      groupBy(attrByIndex(_, ADDRESS_SHORT_INDEX))
      .values
      .toSeq
      .sortBy(index)
      .map(l => l.map(_._1))
      .foldLeft(List.empty[List[JArrayList[String]]])((b, a) => addToGroup(listAddress = a, source = b, partitionCnt = partitionCnt))
      .reverse
  }

  def group(bills: Seq[(JArrayList[String], Int)], partitionCnt: Int) = {
    bills
      .groupBy(attrByIndex(_, POSTAL_INDEX))
      .values
      .toSeq
      .sortBy(index)
      .flatMap(listPostal => splitByPartition(listPostal.toList, partitionCnt = partitionCnt))
  }

  def run(bills: JList[JArrayList[String]]): Seq[List[JArrayList[String]]] = {
    info(s"start grouping size = ${bills.size}")
    group(bills = bills.asScala.zipWithIndex, partitionCnt = AppConfig.partition)
  }

}
