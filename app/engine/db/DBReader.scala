package engine.db

import java.sql.{Connection, DriverManager}
import java.util.{Date, ArrayList => JArrayList, List => JList}

import config.DBConfig
import engine.SQLBuilder
import play.Logger._
import services.ExportService
import services.parameters.{CisDivision, MkdChs}

import scala.collection.JavaConverters._
import scala.util.Try

object DBReader {

  def log(l: JList[JArrayList[String]]) = {
    l.asScala.map(s => s.asScala.mkString("~")).foreach(info)
  }

  def getPremiseCode(premId: String): Array[String] = {
    Class.forName(DBConfig.driver)
    val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)
    try {
      val st = conn.prepareStatement(SQLBuilder.queryHistoryById)
      try {
        st.setString(1, premId)
        val rs = st.executeQuery()
        if (rs.next) Array(rs.getString(1)) else Array.empty[String]
      } finally {
        if (st != null) st.close()
      }
    } finally {
      conn.close()
    }
  }

  private def readFromDb(read: ExportService => JList[JArrayList[String]],
                         writeToLog: (Connection, String) => Unit) = {
    Try {
      Class.forName(DBConfig.driver)
      val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)
      try {
        writeToLog(conn, s"start read bills from db")

        val service = new ExportService(conn)
        val l = read(service)
        //        log(l)
        writeToLog(conn, s"end read bills from db (size = ${l.size()})")

        l
      } finally {
        if (conn != null) conn.close
      }
    }
  }

  def readBillsFromDb(dt: Date,
                      mkdPremiseId: String,
                      code: String,
                      removeFromActiveKey: String => Unit): Try[List[JArrayList[String]]] = {
    def getBills(dt: Date, mkdPremiseId: String)(service: ExportService) =
      service.getBills(dt, mkdPremiseId)

    try {
      readFromDb(read = getBills(dt, mkdPremiseId), writeToLog = (c, s) => ()).map(l => l.asScala.toList)
    } finally {
      removeFromActiveKey(code)
    }
  }

  def readBillsFromDb(dt: Date,
                      mkdChs: MkdChs,
                      cisDivision: CisDivision,
                      code: String,
                      dbLogWriter: DBLogWriter,
                      removeFromActiveKey: String => Unit): Try[List[JArrayList[String]]] = {

    def getBills(dt: Date, mkdChs: MkdChs, cisDivision: CisDivision, code: String)(service: ExportService) =
      service.getBills(dt, mkdChs, cisDivision, code)

    try {
      readFromDb(read = getBills(dt, mkdChs, cisDivision, code), writeToLog = dbLogWriter.log(code)).map(l => l.asScala.toList)
    } finally {
      removeFromActiveKey(code)
    }
  }

}
