package engine

import java.util.{ArrayList => JArrayList}

import scala.language.implicitConversions

object Utl {

  def createKvit(postal: Int, address: Int, id: Int): JArrayList[String] = {

    val l = new JArrayList[String]()

    l.add("01")
    l.add(s"postal_$postal")
    l.add(s"address_$address")
    l.add(s"address2_$address")
    l
  }

}
