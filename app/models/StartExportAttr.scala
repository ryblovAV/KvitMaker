package models

import java.util.Calendar

import services.parameters.{CisDivision, MkdChs}

case class StartExportAttr(month: Int,
                           year: Int,
                           mkdType: String,
                           division: String,
                           mkdPremiseId: String,
                           codeArrayStr: String,
                           orderBy: Int) {
  def dt = {
    val calendar = Calendar.getInstance
    calendar.set(year, month-1, 1)
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

  def isFindByPremiseId = !mkdPremiseId.isEmpty

  val codeArray = StartExportAttr.strToArray(codeArrayStr)

  def key(code: String) = s"$month~$year~$code"

  def orderByIndex = if (orderBy == 1) true else false

}

object StartExportAttr {
  def intCodeToString(code: Int) = if (code < 10) s"0$code" else code.toString

  def strToArray(str: String) =
    if (str.isEmpty) Array.empty[String]
    else str
      .split(";")
      .map(_.split("-").map(s => s.toInt))
      .map(a => if (a.length == 1) List(a.head) else (a.head to a.last).toList)
      .flatMap(c => c.map(intCodeToString))
      .distinct
}
