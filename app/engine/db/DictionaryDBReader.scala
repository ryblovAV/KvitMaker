package engine.db

import java.sql.DriverManager

import config.DBConfig

import scala.util.Try

object DictionaryDBReader {

  val sql =
    s"""
       |  select extractValue(XMLType(lv.BO_DATA_AREA), 'tarrifTable/company') company
       |    from rusadm.F1_EXT_LOOKUP_VAL lv
       |   where trim(lv.bus_obj_cd) =
       |    case
       |      when to_number(?) between 1 and 18 then 'CM_EL_ORG'
       |      else 'CM_EL_ORG_G'
       |    end
       |    and nvl(extractValue(XMLType(lv.BO_DATA_AREA), 'tarrifTable/kod_bd'), ?)  = ?
       |    and rownum = 1
     """.stripMargin

     def readFromDb(code: String):Try[String] = {
       Try {
         Class.forName(DBConfig.driver)
         val conn = DriverManager.getConnection(DBConfig.url, DBConfig.username, DBConfig.password)
         try {

           val st = conn.prepareStatement(sql)

           st.setString(1,code)
           st.setString(2,code)
           st.setString(3,code)

           val rs = st.executeQuery()
           if (rs.next()) rs.getString(1) else s"Unknown for code = $code"

         } finally {
           if (conn != null) conn.close
         }
       }



     }


}
