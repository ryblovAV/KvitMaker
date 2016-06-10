package parser

import models.StartExportAttr
import org.scalatest.FunSuite

class ParseCodeToArray extends FunSuite {

  val sEmpty = Seq("")

  val sListValues = "01;02;05"

  val sRange = Map(
    "01-05" -> Array("01","02","03","04","05"),
    "01-03;04-06" -> Array("01","02","03","04","05","06"),
    "01-03;05;06-08" -> Array("01","02","03","05","06","07","08"),
    "99;13;01;01-05" -> Array("99","13","01","02","03","04","05"),
    "99;01;01-05;13" -> Array("99","01","02","03","04","05","13"),
    "01-05;99;01;13" -> Array("01","02","03","04","05","99","13"),
    "01-05;99;13;01" -> Array("01","02","03","04","05","99","13")
  )

  test(s"parse: $sListValues ") {
    assert(StartExportAttr.strToArray(sListValues) === Array("01","02","05"))
  }

  test(s"parse empty string") {
    sEmpty.foreach(s =>
      assert(StartExportAttr.strToArray(s) === Array.empty[String])
    )
  }

  test(s"parse range string") {
    sRange.foreach{
      case (s,a) => assert(StartExportAttr.strToArray(s) === a)
    }
  }

}
