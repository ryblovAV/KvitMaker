package engine.db

import java.sql.{Connection, DriverManager}

import config.DBConfig
import engine.SQLBuilder
import play.Logger._

object PreDBWriter {

  def createTable(tableName: String, createTableSql: Seq[String]) = {

    def checkExists(conn: Connection, tableName: String) = {
      val st = conn.prepareStatement(SQLBuilder.checkExistsTable)
      try {
        st.setString(1, tableName)
        val rs = st.executeQuery()
        rs.next()
      } finally {
        if (st != null) st.close()
      }
    }

    def createTable(conn: Connection, createTableSql: Seq[String]) = {
      val st = conn.createStatement()
      try {
        info("start create table")
        createTableSql.foreach(sql => st.executeUpdate(sql))
        info("create table complete")
      } finally {
        if (st != null) st.close()
      }
    }

      Class.forName(DBConfig.driver)
      val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)
      try {
        if (!checkExists(conn, tableName)) {
          createTable(conn,createTableSql)
        }
      } finally {
        if (conn != null) conn.close
      }
  }

  def preLoadProgressJournal = {
    createTable(SQLBuilder.journalTableName, Seq(SQLBuilder.createTableJournal, SQLBuilder.createPKJournal))
  }

  def preLoadHistoryJournal = {
    createTable(SQLBuilder.historyTableName, Seq(SQLBuilder.createTableHistory, SQLBuilder.createPKHistory))
  }

}
