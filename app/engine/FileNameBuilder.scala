package engine

import java.text.SimpleDateFormat
import java.util.Date

import services.parameters.{CisDivision, MkdChs}

object FileNameBuilder {

  val df = new SimpleDateFormat("MM.yyyy")

  def mkdToStr(mkdChs: MkdChs) = mkdChs match {
    case MkdChs.MKD => "_mkd"
    case _ => ""
  }

  def cisDivisionToStr(cisDivision: CisDivision) = cisDivision match {
    case CisDivision.GESK => "GESK"
    case CisDivision.LESK => "LESK"
  }

  def numToStr(num: Int) = if (num == 0) "" else s"_$num"

  def createCSVFileName(mkdChs: MkdChs)(code: String, num: Int) =
    s"Ab${mkdToStr(mkdChs)}_${code}${numToStr(num)}.csv"

  def createPremiseCSVFileName(mkdChs: MkdChs)(code: String, num: Int) =
    s"Ab_premise_${mkdToStr(mkdChs)}_${code}${numToStr(num)}.csv"

  def rootPath(processId: String) = s"data//$processId"

  def rootPathSource(processId: String) = s"${rootPath(processId)}//source"

  def arhivePath(processId: String) = s"${rootPath(processId)}//archive_$processId.zip"

  def createFullRegistryFileName(dt: Date) = s"reestr_${df.format(dt)}_full.xlsx"

  def createRegistryFileName(code: String, dt: Date, num: Int) =
    s"reestr_${df.format(dt)}_${code}${numToStr(num)}.xlsx"

  def createPath(processId: String,
                 mkdChs: MkdChs,
                 cisDivision: CisDivision)
                (code: String) =
    s"${rootPathSource(processId)}//Ab${mkdToStr(mkdChs)}_${cisDivisionToStr(cisDivision)}_${code}_$processId"

  def createPremisePath(processId: String,
                 mkdChs: MkdChs,
                 cisDivision: CisDivision)
                (code: String) =
    s"${rootPathSource(processId)}//Ab_premise_${mkdToStr(mkdChs)}_${cisDivisionToStr(cisDivision)}_${code}_$processId"


}
