package engine.file

import java.io.File
import java.util.{Date, ArrayList => JArrayList, List => JList}

import config.AppConfig
import engine.{FileNameBuilder, RegistryUnionRow, RegistryBuilder}
import org.apache.commons.io.FileUtils
import org.zeroturnaround.zip.ZipUtil
import play.Logger._
import services.parameters.MkdChs

import scala.collection.immutable.IndexedSeq

object FileEngine {

  def makeZip(processId: String) = {

    val archivePath = FileNameBuilder.arhivePath(processId)
    info(s"start make archive, processId = $processId")
    ZipUtil.pack(
      new File(FileNameBuilder.rootPathSource(processId)),
      new File(archivePath)
    )
    info(s"complete make archive, processId = $processId")
    archivePath
  }

  def makeAllFile(processId: String,
                  mkdChs: MkdChs,
                  code: String,
                  dt: Date,
                  createPath: (String) => String,
                  createCSVFileName: (String, Int) => String,
                  groupBills: List[List[JArrayList[String]]]
                 ) = {

    info(s"processId = $processId, start make files code = $code")
    val path = createPath(code)
    info(s"path  = $path")

    val registry = RegistryBuilder.makeRegistry(groupBills)

    info(s"make registry complete: ${registry.length}")

    registry.zipWithIndex.foreach {
      case (r, index) =>
        val xlsxFile = makeRegistryFile(code = code, dt = dt, num = index, path = path)
        ExcelEngine.fillDataRegistry(code = code, mkd = mkdChs, registryData = r, file = xlsxFile)
    }

    val fullRegistryFile = makeUnionRegistryFile(dt = dt, path = path)
    info("make full registry file complete")

    val registryUnion = registry
      .zipWithIndex
      .flatMap {
        case (rows, index) => rows
          .groupBy(_.postal)
          .map {
            case (postal, rows) =>
              RegistryUnionRow(
                numPackage = index + 1,
                postal = postal,
                count = rows.foldLeft(0)((s,row) => s + row.count))
          }
      }

    ExcelEngine.fillDataUnionRegistry(
      code = code,
      mkd = mkdChs,
      registryData = registryUnion,
      file = fullRegistryFile)

    //make CSV files
    groupBills.zipWithIndex.map {
      case (bills, index) =>
        makeCSVFile(
          code = code,
          num = index,
          path = path,
          createFileName = createCSVFileName,
          bills = bills)
    }
  }

  def makeCSVFile(code: String,
                  num: Int,
                  path: String,
                  createFileName: (String, Int) => String,
                  bills: List[JArrayList[String]]) = {

    val filePath = s"$path//${createFileName(code, num)}"

    (new File(path)).mkdirs()

    CSVBuilder.writeToFile(new File(filePath), bills)
  }

  private def makeFileFromTemplate(filePath: String, templateFilePath: String) = {
    info(s"start makeFileFromTemplate, filePath = $filePath")
    val excelFile = new File(filePath)
    val templateFile = new File(templateFilePath)
    FileUtils.copyFile(templateFile, excelFile)
    info("create file complete")
    excelFile
  }

  def makeRegistryFile(code: String, dt: Date, num: Int, path: String) = {
    val filePath = s"$path//${FileNameBuilder.createRegistryFileName(code, dt, num)}"
    makeFileFromTemplate(filePath = filePath, templateFilePath = AppConfig.templatePath)
  }

  def makeUnionRegistryFile(dt: Date, path: String) = {
    val filePath = s"$path//${FileNameBuilder.createFullRegistryFileName(dt)}"
    makeFileFromTemplate(filePath = filePath, templateFilePath = AppConfig.unionTemplatePath)
  }

}
