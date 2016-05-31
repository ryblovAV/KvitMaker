package engine

object SQLBuilder {

  val journalTableName = "cm_make_kvit_journal"

  def createTableJournal =
    s"""
       |create table $journalTableName
       |(
       |  process_id  varchar2(100),
       |  code        varchar2(500),
       |  dt          date default sysdate,
       |  message     varchar2(4000)
       |)
     """.stripMargin

  def createPKJournal =
    s"alter table $journalTableName add constraint PK_$journalTableName primary key (process_id, code)"

  def checkExistsTable =
    s"select t.table_name from user_tables t where t.table_name = upper(?)"

  def messageJournal =
    s"""
       |merge into $journalTableName j
       |using (select ? as process_id, ? as code, ? as message from dual) j2
       |on (j.process_id = j2.process_id and j.code = j2.code)
       |when matched then
       | update set j.dt = sysdate, j.message = j2.message
       |when not matched then
       | insert
       |  (j.process_id, j.code, j.dt, j.message)
       | values
       |  (j2.process_id, j2.code, sysdate, j2.message)
       """.stripMargin

  def queryProgress =
    s"select code, to_char(dt,'DD.MM.YYYY HH24:MI:SS') as dt, message from $journalTableName where process_id = ?"

}
