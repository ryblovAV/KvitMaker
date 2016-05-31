package engine.db

import java.sql.{Connection, DriverManager}

import config.DBConfig
import engine.SQLBuilder
import play.Logger._

object PreDBWriter {

  def preLoadJournal() = {

    def checkExists(conn: Connection) = {
      val st = conn.prepareStatement(SQLBuilder.checkExistsTable)
      try {
        st.setString(1, SQLBuilder.journalTableName)
        val rs = st.executeQuery()
        rs.next()
      } finally {
        st.close()
      }
    }

    def createTabel(conn: Connection) = {
      val st = conn.createStatement()
      try {
        st.executeUpdate(SQLBuilder.createTableJournal)
        st.executeUpdate(SQLBuilder.createPKJournal)
        info("create table complete")
      } finally {
        st.close()
      }
    }

      Class.forName(DBConfig.driver)
      val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)
      try {
        if (!checkExists(conn)) {
          createTabel(conn)
        }
      } finally {
        if (conn != null) conn.close
      }
  }

}
