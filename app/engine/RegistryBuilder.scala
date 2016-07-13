package engine

import java.util.{ArrayList => JArrayList}

import models.Kvit._

case class RegistryRow(address: String, postal: String, first: Int, last: Int, count: Int)

case class RegistryUnionRow(numPackage: Int, postal: String, count: Int)

object RegistryBuilder {

  def prevCount(b: List[(List[JArrayList[String]],Int)]): Int = b match {
    case h::_ => h._2 + h._1.length
    case Nil => 0
  }

  def makeRegistry(billsGroups: List[List[JArrayList[String]]]) = {
    billsGroups
      .foldLeft(List.empty[(List[JArrayList[String]],Int)])((b,l) => (l, prevCount(b))::b)
      .reverse
      .map(e => makeRegistryOne(e._1,e._2))
  }

  def makeRegistryOne(l: List[JArrayList[String]], delta: Int): Seq[RegistryRow] = {

    def calc(address: String, postal: String, l: List[(JArrayList[String],Int)]) =
      RegistryRow(
        address = address,
        postal = postal,
        first = delta + l.head._2 + 1,
        last = delta + l.last._2 + 1,
        count = l.length)

    l.zipWithIndex
      .groupBy(p => (GroupEngine.attrByIndex(p, ADDRESS_SHORT_INDEX) , GroupEngine.attrByIndex(p, POSTAL_INDEX)))
      .toSeq
      .sortBy {
        case ((adress,postal), (values, index) :: _) => index
        case _ => 1
      }
      .map {
        case ((adress,postal), l) => calc(adress, postal, l)
      }
  }

}
