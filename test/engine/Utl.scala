package engine

import java.util.{Map => JMap}

import models.Kvit._

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object Utl {

  def createKvit(postal: Int, address: Int, id: Int): JMap[String, String] = {
    Map(POSTAL -> s"postal_$postal",
        ADDRESS_SHORT -> s"address_$address",
        ID -> id.toString).asJava
  }

}
