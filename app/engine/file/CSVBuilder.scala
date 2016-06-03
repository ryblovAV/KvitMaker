package engine.file

import java.io.{BufferedWriter, File, FileOutputStream, OutputStreamWriter}
import java.util.{ArrayList => JArrayList}

import scala.collection.JavaConverters._

object CSVBuilder {

  /**
    * Символ конца строки в CSV-файле.
    */
  val endLine = "\n"

  /**
    * Разделитель ячеек в CSV-файле.
    */
  val separator = "\t"

  /**
    * Кодировка CSV-файла.
    */
  val encoding = "Cp1251"

  def writeToFile(exportFile: File, bills: List[JArrayList[String]]) = {

    def formatString(s: String) = if (s.indexOf(separator) >= 0) "\""+s+"\""  else s

    def writeString(bill: JArrayList[String], writer: BufferedWriter) = {
      bill.asScala.drop(3).map(formatString).map(_+separator).foreach(writer.append)
      writer.append(endLine)
    }

    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(exportFile), encoding))

    try {
      bills.foreach(writeString(_,writer))
    } finally {
      writer.flush()
      writer.close()
    }

    exportFile.getName
  }

}
