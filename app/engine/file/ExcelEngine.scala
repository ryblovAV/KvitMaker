package engine.file

import java.io.{File, FileInputStream, FileOutputStream}

import config.AppConfig._
import engine.{CompanyDictionary, RegistryRow, RegistryUnionRow}
import org.apache.poi.ss.usermodel.{Cell, CellCopyPolicy, CellStyle, Row}
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFSheet, XSSFWorkbook}
import services.parameters.MkdChs

import scala.collection.JavaConverters._

object ExcelEngine {

  type CellMaker = (XSSFSheet) => (Int, Int, Cell) => Unit

  type CellSetter = XSSFCell => Unit

  private def replaceTitleValue(code: String,
                                mkd: MkdChs,
                                sheet: XSSFSheet)
                               (rowNum: Int, columnNum: Int, cell: Cell): Unit = {

    def getMKD(mkd: MkdChs) = mkd match {
      case MkdChs.CHS => "Индивидуальные дома"
      case MkdChs.MKD => "Многоквартирные дома"
    }

    cell.getStringCellValue match {
      case COMPANY_TAG => cell.setCellValue(CompanyDictionary.get(code))
      case MKD_TAG => cell.setCellValue(getMKD(mkd))
      case _ =>
    }
  }

  private def replaceUnionSummaryValue(registryData: Seq[RegistryUnionRow],
                                       sheet: XSSFSheet)
                                      (rowNum: Int, columnNum: Int, cell: Cell): Unit = {

    cell.getStringCellValue match {
      case PACKAGES_TAG => cell.setCellValue(registryData.length)
      case COUNT_BILLS_TAG => cell.setCellValue(registryData.foldLeft(0)((s, r) => s + r.count))
      case _ =>
    }
  }

  private def replaceSummaryValue(registryData: Seq[RegistryRow],
                                  sheet: XSSFSheet)
                                 (rowNum: Int, columnNum: Int, cell: Cell): Unit = {

    cell.getStringCellValue match {
      case COUNT_ALL_TAG => cell.setCellValue(registryData.map(_.count).sum)
      case _ =>
    }
  }

  private def merge(sheet: XSSFSheet)(rowNum: Int, columnNum: Int, cell: Cell): Unit = {
    cell.getStringCellValue match {
      case MERGE_TAG => sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 2, 4))
      case _ =>
    }
  }

  def addRow(workSheet: XSSFSheet,
             cellStyle: CellStyle,
             rowNum: Int,
             columnNum: Int,
             setValues: Seq[CellSetter]
            ) = {

    val row = workSheet.createRow(rowNum)

    setValues.zipWithIndex.foreach { case (cellSetter, index) =>
      val cell = row.createCell(columnNum + index)
      cell.setCellStyle(cellStyle)
      cellSetter(cell)
    }
  }

  def fillRegistry(registryData: Seq[RegistryRow])
                  (sheet: XSSFSheet,
                   cellStyle: CellStyle,
                   rowNum: Int,
                   columnNum: Int) = {

    registryData.zipWithIndex.foreach {
      case (r, i) =>

        val setValues: Seq[CellSetter] = Seq(
          cell => cell.setCellValue(r.address),
          cell => cell.setCellValue(r.postal),
          cell => cell.setCellValue(r.first),
          cell => cell.setCellValue(r.last),
          cell => cell.setCellValue(r.count)
        )

        addRow(
          workSheet = sheet,
          cellStyle = cellStyle,
          rowNum = rowNum + i,
          columnNum = columnNum,
          setValues = setValues
        )
    }
  }

  def fillUnionRegistry(registryData: Seq[RegistryUnionRow])
                       (sheet: XSSFSheet,
                        cellStyle: CellStyle,
                        rowNum: Int,
                        columnNum: Int) = {

    registryData.zipWithIndex.foreach {
      case (r, i) =>
        val setValues: Seq[CellSetter] = Seq(
          cell => cell.setCellValue(s"Пачка №${i + 1}"),
          cell => cell.setCellValue(r.postal),
          cell => cell.setCellValue(1),
          cell => cell.setCellValue(r.count)
        )
        addRow(
          workSheet = sheet,
          cellStyle = cellStyle,
          rowNum = rowNum + i,
          columnNum = columnNum,
          setValues = setValues
        )
    }
  }

  private def fillBody(fillCellValues: (XSSFSheet, CellStyle, Int, Int) => Unit, registryDataLength: Int, sheet: XSSFSheet)
                      (rowNum: Int, columnNum: Int, cell: Cell): Unit = {

    cell.getStringCellValue match {
      case BODY_TAG =>
        if (registryDataLength > 1)
          sheet.copyRows(List(sheet.getRow(rowNum + 1)).asJava, rowNum + registryDataLength, new CellCopyPolicy())

        fillCellValues(sheet, cell.getCellStyle, rowNum, columnNum)
      case _ =>
    }
  }

  private def makeSheet(sheet: XSSFSheet, makeCell: (Int, Int, Cell) => Unit) = {

    def makeRow(row: Row, makeCell: (Int, Int, Cell) => Unit) = {
      for (cellNum <- row.getFirstCellNum.toInt to row.getLastCellNum) {
        val cell = row.getCell(cellNum)
        if ((cell != null) && (cell.getCellType() == Cell.CELL_TYPE_STRING)) {
          makeCell(row.getRowNum, cellNum, cell)
        }
      }
    }

    for (i <- sheet.getFirstRowNum to sheet.getLastRowNum) {
      val row = sheet.getRow(i)
      if (row != null)
        makeRow(row = row, makeCell = makeCell)
    }
  }

  private def fillData(cellMakers: Seq[CellMaker], file: File) = {
    val in = new FileInputStream(file)

    val workbook = new XSSFWorkbook(in)
    val sheet = workbook.getSheetAt(0)

    cellMakers.foreach(cellMakers => makeSheet(sheet = sheet, makeCell = cellMakers(sheet)))

    val out = new FileOutputStream(file)
    workbook.write(out)
    out.close
  }

  def fillDataRegistry(code: String,
                       mkd: MkdChs,
                       registryData: Seq[RegistryRow],
                       file: File) = {

    fillData(cellMakers = Seq(
      sheet => replaceTitleValue(code = code, mkd = mkd, sheet = sheet),
      sheet => replaceSummaryValue(registryData, sheet = sheet),
      sheet => fillBody(fillCellValues = fillRegistry(registryData), sheet = sheet, registryDataLength = registryData.length),
      sheet => merge(sheet = sheet)
    ), file)

  }

  def fillDataUnionRegistry(code: String,
                            mkd: MkdChs,
                            registryData: Seq[RegistryUnionRow],
                            file: File) = {
    fillData(cellMakers = Seq(
      sheet => replaceTitleValue(code = code, mkd = mkd, sheet = sheet),
      sheet => replaceUnionSummaryValue(registryData, sheet = sheet),
      sheet => fillBody(fillCellValues = fillUnionRegistry(registryData), sheet = sheet, registryDataLength = registryData.length)
    ), file)
  }

}

