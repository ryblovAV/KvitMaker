package engine.db

import java.sql.DriverManager

import config.DBConfig
import engine.{ProcessResult, SQLBuilder}

import scala.collection.mutable.ArrayBuffer

case class ProgressInfo(code: String, dtStart: String, dt: String, message: String)

object DBLogReader {

  val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)

  def readProgress(processId: String) = synchronized {
    val st = conn.prepareStatement(SQLBuilder.queryProgress)
    st.setString(1,processId)
    val rs = st.executeQuery()

    val progress = ArrayBuffer.empty[ProgressInfo]
    while (rs.next()) {
      progress += ProgressInfo(
        code = rs.getString(1),
        dtStart = rs.getString(2),
        dt = rs.getString(3),
        message = rs.getString(4))
    }
    progress
  }



}
