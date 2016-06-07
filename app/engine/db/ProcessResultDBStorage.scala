package engine.db

import java.sql.{DriverManager, ResultSet, Date => SQLDate}
import java.util.{Calendar, Date}

import config.{AppConfig, DBConfig}
import engine.SQLBuilder

import scala.collection.mutable.ArrayBuffer

case class ProcessResult(processId: String,
                         fileName: String,
                         ip:String,
                         dt:Date = Calendar.getInstance().getTime,
                         codeArray: Array[String],
                         month: Int,
                         mkdType :String,
                         mkdPremiseId: String
                        ) {
  def date: String = AppConfig.df.format(dt)
}


object ProcessResultDBStorage {


  PreDBWriter.preLoadHistoryJournal

  private def createProcessResult(rs: ResultSet) = {
      ProcessResult(
        processId = rs.getString(1),
        fileName = rs.getString(2),
        ip = rs.getString(3),
        dt = rs.getDate(4),
        codeArray = rs.getString(5).split(";"),
        mkdPremiseId = rs.getString(6),
        month = rs.getInt(7),
        mkdType = rs.getString(8)
      )
  }

  def getProcesses: List[ProcessResult] = {

    val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)

    try {

      val st = conn.createStatement()
      try {
        val rs = st.executeQuery(SQLBuilder.queryHistory)

        val processResults = ArrayBuffer.empty[ProcessResult]

        while (rs.next) {
          processResults += createProcessResult(rs)
        }

        processResults.toList.sortBy(e => e.dt.getTime)

      } finally {
        if (st != null) st.close()
      }

    } finally {
      if (conn != null) conn.close()
    }

  }

  def getFileNameOpt(processId: String):Option[String] = {
    readHistoryById(processId).map(_.fileName)
  }

  def getFileName(processId: String) =  getFileNameOpt(processId) match {
    case Some(fileName) => fileName
    case None => ""
  }

  private def readHistoryById(processId: String):Option[ProcessResult] = {

    val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)

    try {

      val st = conn.prepareStatement(SQLBuilder.queryHistoryById)
      try {

        st.setString(1,processId)
        val rs = st.executeQuery()
        if (rs.next) Some(createProcessResult(rs)) else None

      } finally {
        if (st != null) st.close()
      }

    } finally {
      if (conn != null) conn.close()
    }

  }

  def addProcessResult(result: ProcessResult) = {
    val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)

    try {

      val st = conn.prepareStatement(SQLBuilder.messageHistory)
      try {

        st.setString(1, result.processId)
        st.setString(2, result.fileName)
        st.setString(3, result.ip)
        st.setDate(4, new SQLDate(result.dt.getTime))
        st.setString(5, result.codeArray.mkString(";"))
        st.setString(6, result.mkdPremiseId)
        st.setInt(7, result.month)
        st.setString(8, result.mkdType)
        st.executeUpdate()

      } finally {
        if (st != null) st.close()
      }

    } finally {
      if (conn != null) conn.close()
    }

  }


}
