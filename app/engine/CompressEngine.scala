package engine

import java.util.{List => JList, Map => JMap}

import play.Logger._

import scala.language.implicitConversions

object CompressEngine {

  def compress(bills: JList[JMap[String,String]]) = {
    info(s"start compress bills.size = ${bills.size}")
    "test.zip"
//    var i = 0
//    while (i < bills.size) {
//      info(s"bill: ${bills.get(i)}")
//      i += 1
//    }
  }

}
