package engine

import org.scalatest.FunSuite

import Utl._
import java.util.{Map => JMap}

class GroupTest extends FunSuite {

  def fill(cntPostal: Int, cntAddress: Int, cnt: Int):List[JMap[String,String]] = {
    (0 until cntPostal).flatMap(postal =>
      (0 until cntAddress).flatMap(address =>
        (0 until cnt).map(i =>
          createKvit(
            postal = postal,
            address = address,
            id = postal * cntAddress * cnt + address * cnt + i)))).toList
  }

  test("group"){
    val kvits = fill(cntPostal = 2, cntAddress = 4, cnt = 3)

    val res = ExportEngine.group(kvits = kvits.zipWithIndex, partitionCnt = 7)

    val (l1, k1) = kvits.splitAt(9)

    val (l2, k2) = k1.splitAt(3)

    val (l3, l4) = k2.splitAt(9)

    val correct = List(l1,l2,l3,l4)

    assert(res === correct)
  }


}
