package engine.db

import java.sql.Connection

import engine.SQLBuilder

class DBLogWriter(processId: String) {

  PreDBWriter.preLoadJournal()

  def log(code: String)(conn: Connection, message: String):Unit  = {
    val st = conn.prepareStatement(SQLBuilder.messageJournal)

    try {
      st.setString(1, processId)
      st.setString(2, code)
      st.setString(3, message)
      st.executeUpdate()
    } finally {
      st.close()
    }

  }

}
