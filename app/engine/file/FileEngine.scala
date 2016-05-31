package engine.file

import java.io.{BufferedWriter, File, FileWriter}
import java.util.{List => JList, Map => JMap}

import models.Kvit

object FileEngine {

  def makeCSVFile(processId: String, code: String, num: Int, bills: List[JMap[String,String]]) = {

    def directoryPath(processId: String, code: String) = s"data//$processId//$code"

    def filePath(directoryPath: String, num: Int) = s"$directoryPath//file-$num.csv"

    def makeString(bill: JMap[String,String]) =
      bill.get(Kvit.POSTAL) + "~" + bill.get(Kvit.ADDRESS_SHORT)

    val path = directoryPath(processId,code)
    (new File(path)).mkdirs()

    val file = new File(filePath(path,num))
    val bufferWriter = new BufferedWriter(new FileWriter(file))

    bills.foreach(bill => bufferWriter.write(makeString(bill)))

    bufferWriter.close()
  }


}
