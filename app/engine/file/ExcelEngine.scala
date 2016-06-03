package engine.file

import java.io.{File, FileInputStream, FileOutputStream}

import config.AppConfig._
import engine.{CompanyDictionary, RegistryRow}
import org.apache.poi.ss.usermodel.{Cell, CellCopyPolicy, CellStyle, Row}
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}
import services.parameters.MkdChs

import scala.collection.JavaConverters._

object ExcelEngine {

  private def replaceValue(code: String,
                   mkd: MkdChs,
                   registryData: Seq[RegistryRow],
                   sheet: XSSFSheet)
                  (rowNum: Int, columnNum: Int, cell: Cell): Unit = {

    def getMKD(mkd: MkdChs) = mkd match {
      case MkdChs.CHS => "Индивидуальные дома"
      case MkdChs.MKD => "Многоквартирные дома"
    }

    cell.getStringCellValue match {
      case COMPANY_TAG => cell.setCellValue(CompanyDictionary.get(code))
      case MKD_TAG => cell.setCellValue(getMKD(mkd))
      case COUNT_ALL_TAG => cell.setCellValue(registryData.map(_.count).sum)
      case _ =>
    }
  }

  private def merge(sheet: XSSFSheet)(rowNum: Int, columnNum: Int, cell: Cell): Unit = {
    cell.getStringCellValue match {
      case MERGE_TAG => sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 1, 3))
      case _ =>
    }
  }

  private def fillBody(registryData: Seq[RegistryRow], sheet: XSSFSheet)(rowNum: Int, columnNum: Int, cell: Cell): Unit = {

    def fillRegistry(sheet: XSSFSheet,
                     cellStyle: CellStyle,
                     rowNum: Int,
                     columnNum: Int,
                     registryData: Seq[RegistryRow]) = {

      def addRow(workSheet: XSSFSheet,
                 cellStyle: CellStyle,
                 rowNum: Int,
                 columnNum: Int,
                 registryRow: RegistryRow) = {

        val row = workSheet.createRow(rowNum)

        val addressCell = row.createCell(columnNum)
        addressCell.setCellStyle(cellStyle)
        addressCell.setCellValue(registryRow.address)

        val firstCell = row.createCell(columnNum + 1)
        firstCell.setCellStyle(cellStyle)
        firstCell.setCellValue(registryRow.first)

        val lastCell = row.createCell(columnNum + 2)
        lastCell.setCellStyle(cellStyle)
        lastCell.setCellValue(registryRow.last)

        val countCell = row.createCell(columnNum + 3)
        countCell.setCellStyle(cellStyle)
        countCell.setCellValue(registryRow.count)
      }

      if (registryData.length > 1)
        sheet.copyRows(List(sheet.getRow(rowNum + 1)).asJava, rowNum + registryData.length, new CellCopyPolicy())

      registryData.zipWithIndex.foreach {
        case (r, i) => addRow(workSheet = sheet, cellStyle = cellStyle, rowNum = rowNum + i, columnNum = columnNum, registryRow = r)
      }
    }

    cell.getStringCellValue match {
      case BODY_TAG => fillRegistry(sheet = sheet, cellStyle = cell.getCellStyle, rowNum = rowNum, columnNum = columnNum, registryData = registryData)
      case _ =>
    }
  }

  private def makeSheet(sheet: XSSFSheet, makeCell: (Int,Int,Cell) => Unit) = {

    def makeRow(row: Row, makeCell: (Int,Int,Cell) => Unit) = {
      for (cellNum <- row.getFirstCellNum.toInt to row.getLastCellNum) {
        val cell = row.getCell(cellNum)
        if ((cell != null) && (cell.getCellType() == Cell.CELL_TYPE_STRING)) {
          makeCell(row.getRowNum,cellNum,cell)
        }
      }
    }

    for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
      val row = sheet.getRow(i)
      if (row != null)
        makeRow(row = row, makeCell = makeCell)
    }
  }

  def fillData(code: String,
               mkd: MkdChs,
               registryData: Seq[RegistryRow],
               file: File) = {

    val in = new FileInputStream(file)

    val workbook = new XSSFWorkbook(in)
    val sheet = workbook.getSheetAt(0)

    makeSheet(sheet = sheet, makeCell = replaceValue(code = code, mkd = mkd, registryData = registryData, sheet = sheet))
    makeSheet(sheet = sheet, makeCell = fillBody(registryData = registryData, sheet = sheet))
    makeSheet(sheet = sheet, makeCell = merge(sheet = sheet))

    val out = new FileOutputStream(file)
    workbook.write(out)
    out.close
  }

}

