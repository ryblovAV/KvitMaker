package engine

import java.util.{ArrayList => JArrayList, List => JList}

import config.AppConfig
import models.Kvit._
import play.Logger._

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

  def index(a: Seq[(JArrayList[String], Int)]) = a.head match {
      case (_,i) => i
  }

  def attrByIndex(a: (JArrayList[String], Int), attrIndex: Int) = a match {
    case (e, i) => e.get(attrIndex)
  }

  def splitByPartition(listPostal: List[(JArrayList[String], Int)],
                       partitionCnt: Int) = {

    def getAddressAttr(a: (JArrayList[String], Int)): String = a match {
      case (e, i) => s"${e.get(ADDRESS_SHORT_INDEX)}~${e.get(ADDRESS2_INDEX)}"
    }

    listPostal.
      groupBy(getAddressAttr(_))
      .values
      .toSeq
      .sortBy(index)
      .map(l => l.map(_._1))
      .foldLeft(List.empty[List[JArrayList[String]]])((b, a) => addToGroup(listAddress = a, source = b, partitionCnt = partitionCnt))
      .reverse
  }

  def group(bills: List[(JArrayList[String], Int)], partitionCnt: Int) = {

    def getPostal(a: (JArrayList[String], Int)) = attrByIndex(a, POSTAL_INDEX)

    bills
      .groupBy(getPostal(_))
      .values
      .toList
      .sortBy(index)
      .flatMap(listPostal => splitByPartition(listPostal.toList, partitionCnt = partitionCnt))
  }

  def run(bills: List[JArrayList[String]]): List[List[JArrayList[String]]] = {
    info(s"start grouping size = ${bills.size}")
    group(bills = bills.zipWithIndex, partitionCnt = AppConfig.partition)
  }

}
