package engine.file

import java.io.File
import java.text.SimpleDateFormat
import java.util.{Date, ArrayList => JArrayList, List => JList}

import config.AppConfig
import engine.RegistryBuilder
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil
import play.Logger._
import services.parameters.{CisDivision, MkdChs}

import scala.collection.JavaConverters._

object FileEngine {

  val df = new SimpleDateFormat("MM.yyyy")

  def numToStr(num: Int) = if (num == 0) "" else s"_$num"

  def mkdToStr(mkdChs: MkdChs) = mkdChs match {
    case MkdChs.MKD => "_mkd"
    case _ => ""
  }

  def cisDivisionToStr(cisDivision: CisDivision) = cisDivision match {
    case CisDivision.GESK => "GESK"
    case CisDivision.LESK => "LESK"
  }

  def rootPath(processId: String) = s"data//$processId"

  def rootPathSource(processId: String) = s"${rootPath(processId)}//source"

  def arhivePath(processId: String) = s"${rootPath(processId)}//archive_$processId.zip"

  def makeZip(processId: String) = {

    val archivePath = arhivePath(processId)
    info(s"start make archive, processId = $processId")
    ZipUtil.pack(
      new File(rootPathSource(processId)),
      new File(archivePath)
    )
    info(s"complete make archive, processId = $processId")
    archivePath
  }


  def createPath(rootPath: String,
                 mkdChs: MkdChs,
                 cisDivision: CisDivision,
                 code: String,
                 processId: String) = s"$rootPath//Ab${mkdToStr(mkdChs)}_${cisDivisionToStr(cisDivision)}_${code}_$processId"

  def makeAllFile(processId: String,
                  mkdChs: MkdChs,
                  cisDivision: CisDivision,
                  code: String,
                  dt: Date,
                  groupBills: JList[JList[JArrayList[String]]]
                 ) = {

    info(s"processId = $processId, start make files code = $code")

    val path = createPath(rootPathSource(processId),mkdChs,cisDivision,code,processId)

    val registry = RegistryBuilder.makeRegistry(groupBills.asScala.map(_.asScala.toList).toList)

    registry.zipWithIndex.foreach{
        case (r,index) =>
          val xlsxFile = makeRegistryFile(code = code,dt = dt, num = index, path = path)
          ExcelEngine.fillData(code = code, mkd = mkdChs, registryData = r, file = xlsxFile)
    }

    val fullRegistryFile = makeFullRegistryFile(code = code,dt = dt, path = path)

    ExcelEngine.fillData(
      code = code,
      mkd = mkdChs,
      registryData = registry.flatMap(l => l.toList),
      file = fullRegistryFile)

    groupBills.asScala.toList.zipWithIndex.map {
      case (bills, index) =>
        makeCSVFile(
            processId = processId,
            mkdChs = mkdChs,
            cisDivision = cisDivision,
            code = code,
            dt = dt,
            num = index,
            path = path,
            bills = bills.asScala.toList)
    }


  }

  def makeCSVFile(processId: String,
                  mkdChs: MkdChs,
                  cisDivision: CisDivision,
                  code: String,
                  dt: Date,
                  num: Int,
                  path: String,
                  bills: List[JArrayList[String]]) = {


    val filePath = s"$path//Ab${mkdToStr(mkdChs)}_${code}${numToStr(num)}.csv"

    (new File(path)).mkdirs()

    CSVBuilder.writeToFile(new File(filePath),bills)
  }

  private def makeFileFromTemplate(filePath: String) = {
    val file = new File(filePath)
    val templateFile = new File(AppConfig.templatePath)

    FileUtils.copyFile(templateFile,file)
    file
  }

  def makeRegistryFile(code: String, dt: Date, num: Int, path: String) = {

    val filePath = s"$path//reestr_${df.format(dt)}_${code}${numToStr(num)}_.xlsx"

    makeFileFromTemplate(filePath = filePath)
  }

  def makeFullRegistryFile(code: String, dt: Date, path: String) = {
    val filePath = s"$path//reestr_${df.format(dt)}_full_.xlsx"
    makeFileFromTemplate(filePath = filePath)
  }



}
