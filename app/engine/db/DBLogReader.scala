package engine.db

import java.sql.DriverManager

import config.DBConfig
import engine.SQLBuilder

import scala.collection.mutable.ArrayBuffer

case class ProgressInfo(code: String, dtStart: String, dt: String, message: String)

object DBLogReader {

  val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)

  def readProgress(processId: String) = synchronized {
    val st = conn.prepareStatement(SQLBuilder.queryProgress)
    val progress = ArrayBuffer.empty[ProgressInfo]
    try {
      st.setString(1,processId)
      val rs = st.executeQuery()

      while (rs.next()) {
        progress += ProgressInfo(
          code = rs.getString(1),
          dtStart = rs.getString(2),
          dt = rs.getString(3),
          message = rs.getString(4))
      }
    } finally {
      if (st != null) st.close()
    }
    progress
  }



}
