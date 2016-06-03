package engine.db

import java.sql.{Connection, DriverManager}
import java.util.{Date, ArrayList => JArrayList, List => JList, Map => JMap}

import config.DBConfig
import services.ExportService
import services.parameters.{CisDivision, MkdChs}

import scala.collection.JavaConverters._
import play.Logger._

import scala.util.Try

object DBReader {

  def log(l: JList[JArrayList[String]]) = {
    l.asScala.map(s => s.asScala.mkString("~")).foreach(info)
  }

  private def readFromDb(read: ExportService => JList[JArrayList[String]],
                         writeToLog: (Connection,String) => Unit) = {
    Try {
      Class.forName(DBConfig.driver)
      val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)
      try {
        writeToLog(conn,s"start read bills from db")

        val service = new ExportService(conn)
        val l = read(service)
//        log(l)
        writeToLog(conn,s"end read bills from db (size = ${l.size()})")

        l
      } finally {
        if (conn != null) conn.close
      }
    }
  }

  def readBillsFromDb(dt: Date, mkdPremiseId: String): Try[JList[JArrayList[String]]] = {
    def getBills(dt: Date, mkdPremiseId: String)(service: ExportService) =
      service.getBills(dt, mkdPremiseId)

    readFromDb(read = getBills(dt, mkdPremiseId),writeToLog = (c,s) => ())
  }

  def readBillsFromDb(dt: Date, mkdChs: MkdChs, cisDivision: CisDivision, code: String, dbLogWriter: DBLogWriter): Try[JList[JArrayList[String]]] = {
    def getBills(dt: Date, mkdChs: MkdChs, cisDivision: CisDivision, code: String)(service: ExportService) =
      service.getBills(dt, mkdChs, cisDivision, code)

    readFromDb(read = getBills(dt, mkdChs, cisDivision, code),writeToLog = dbLogWriter.log(code))
  }

}
