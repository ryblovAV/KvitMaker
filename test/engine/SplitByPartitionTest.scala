package engine

import org.scalatest.FunSuite

import java.util.{ArrayList => JArrayList}

import Utl._

class SplitByPartitionTest extends FunSuite {

  def fillList(postal: Int, cntAddress: Int, cnt: Int) = {
    (0 until cntAddress).toList
      .flatMap(address => (0 until cnt).toList
        .map(i => createKvit(postal = postal, address = address, id = address * cnt + i)))
  }

  def partition(listPostal: List[JArrayList[String]],
              partitionCnt: Int,
              cnt: Int,
              cntAddress: Int) = {

    val n = ((partitionCnt / cnt) + (if (partitionCnt % cnt > 0) 1 else 0)) * cnt
    val correctRes = List(listPostal.take(n),listPostal.takeRight(cntAddress * cnt - n)).filter(!_.isEmpty)

    val res = GroupEngine.splitByPartition(listPostal = listPostal.zipWithIndex,partitionCnt = partitionCnt)

    assert(res === correctRes)
  }

  test("split list 3 * 2 / 3") {
    partition(
      listPostal = fillList(postal = 0, cntAddress = 3, cnt = 2),
      partitionCnt = 3,
      cntAddress = 3,
      cnt = 2)
  }

  test("split list 3 * 2 / 5") {
    partition(
      listPostal = fillList(postal = 0, cntAddress = 3, cnt = 2),
      partitionCnt = 5,
      cntAddress = 3,
      cnt = 2)
  }

  test("split list 3 * 2 / 30") {
    partition(
      listPostal = fillList(postal = 0, cntAddress = 3, cnt = 2),
      partitionCnt = 30,
      cntAddress = 3,
      cnt = 2)
  }

  test("split list 1 * 30 / 30") {
    partition(
      listPostal = fillList(postal = 0, cntAddress = 3, cnt = 2),
      partitionCnt = 30,
      cntAddress = 1,
      cnt = 30)
  }

}
