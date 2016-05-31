package engine.db

import java.sql.{Connection, DriverManager}
import java.util.{Date, List => JList, Map => JMap}

import config.DBConfig
import services.ExportService
import services.parameters.{CisDivision, MkdChs}

import scala.util.Try

object DBReader {

  private def readFromDb(readFromDb: ExportService => JList[JMap[String, String]],
                         writeToLog: (Connection,String) => Unit): Try[JList[JMap[String, String]]] = {
    Try {
      Class.forName(DBConfig.driver)
      val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)
      try {
        writeToLog(conn,s"start read bills from db")

        val service = new ExportService(conn)
        val l = readFromDb(service)

        writeToLog(conn,s"end read bills from db (size = ${l.size()})")

        l
      } finally {
        if (conn != null) conn.close
      }
    }
  }

  def readBillsFromDb(dt: Date, mkdPremiseId: String): Try[JList[JMap[String, String]]] = {
    def getBills(dt: Date, mkdPremiseId: String)(service: ExportService) =
      service.getBills(dt, mkdPremiseId)

    readFromDb(readFromDb = getBills(dt, mkdPremiseId),writeToLog = (c,s) => ())
  }

  def readBillsFromDb(dt: Date, mkdChs: MkdChs, cisDivision: CisDivision, code: String, dbLogWriter: DBLogWriter): Try[JList[JMap[String, String]]] = {
    def getBills(dt: Date, mkdChs: MkdChs, cisDivision: CisDivision, code: String)(service: ExportService) =
      service.getBills(dt, mkdChs, cisDivision, code)

    readFromDb(readFromDb = getBills(dt, mkdChs, cisDivision, code),writeToLog = dbLogWriter.log(code))
  }

}
