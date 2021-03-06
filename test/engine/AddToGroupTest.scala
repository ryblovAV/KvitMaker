package engine

import java.util.{ArrayList => JArrayList}

import engine.Utl._
import org.scalatest.FunSuite

import scala.language.implicitConversions

class AddToGroupTest extends FunSuite{

  def fillSource(postal: Int, cntAddress: Int, cnt: Int): List[List[JArrayList[String]]] = {
    List(
      (0 until cntAddress).toList
        .flatMap(address => (0 until cnt)
          .map(i =>createKvit(postal = postal, address = address, id = address * cnt  + i)
          )
        )
    )
  }

  def fillKvit(postal: Int, address: Int, cnt: Int): List[JArrayList[String]] = {
    (0 until cnt).map(
      id => createKvit(postal = postal, address = address, id = id)
    ).toList
  }


  test("group empty source and target") {
    val listAddress: List[JArrayList[String]] = List.empty[JArrayList[String]]
    val source = List.empty[List[JArrayList[String]]]

    val res = GroupEngine.addToGroup(listAddress = listAddress, source = source, partitionCnt = 100)

    assert(res.length === 0)
  }

  test("group empty source") {
    val listAddress = List.empty[JArrayList[String]]
    val source = fillSource(postal = 1,cntAddress = 10, cnt = 10)

    val res = GroupEngine.addToGroup(listAddress = listAddress, source = source, partitionCnt = 100)

    assert(res === source)
  }

  test("group empty targer (cnt < partition") {
    val listAddress = fillKvit(postal = 0, address = 0, cnt = 90)
    val source = List.empty[List[JArrayList[String]]]

    val res = GroupEngine.addToGroup(listAddress = listAddress,source = source,100)

    assert(res === List(listAddress))

  }

  test("group empty targer (cnt > partition") {
    val listAddress = fillKvit(postal = 0, address = 99, cnt = 3)
    val source = List.empty[List[JArrayList[String]]]

    val res = GroupEngine.addToGroup(listAddress,source,100)

    assert(res === List(listAddress))
  }

  test("group (source cnt > partition") {
    val listAddress = fillKvit(postal = 0, address = 99, cnt = 3)
    val source = fillSource(postal = 0, cntAddress = 3, cnt = 3)

    val res = GroupEngine.addToGroup(listAddress = listAddress,source = source, partitionCnt = 8)

    assert(res === listAddress::source)
  }

  test("group (source cnt < partition") {
    val listAddress = fillKvit(postal = 0, address = 4, cnt = 3)
    val source = fillSource(postal = 0, cntAddress = 3, cnt = 3)

    val res = GroupEngine.addToGroup(listAddress = listAddress,source = source, partitionCnt = 90)

    val res2 = source match {
      case h::t => (h ::: listAddress) :: t
      case Nil => Nil
    }

    assert(res === res2)
  }

}
