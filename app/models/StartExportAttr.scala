package models

import java.util.Calendar

import services.parameters.{CisDivision, MkdChs}

case class StartExportAttr(month: Int,
                           year: Int,
                           mkdType: String,
                           division: String,
                           premId: String,
                           codeArrayStr: String) {
  def dt = {
    val calendar = Calendar.getInstance
    calendar.set(year, month, 1)
    calendar.getTime
  }

  def mkdChs = mkdType match {
    case "MKD" => MkdChs.MKD
    case "CHS" => MkdChs.CHS
  }

  def cisDivision = division match {
    case "LESK" => CisDivision.LESK
    case "GESK" => CisDivision.GESK
  }

  val codeArray = codeArrayStr.split(";")

  def key(code: String) = s"$month~$year~$code"

}
