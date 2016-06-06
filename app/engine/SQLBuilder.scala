package engine

object SQLBuilder {

  val journalTableName = "cm_make_kvit_journal"

  val historyTableName = "cm_kvit_history"

  def createTableJournal =
    s"""
       |create table $journalTableName
       |(
       |  process_id  varchar2(100),
       |  code        varchar2(500),
       |  dt          date default sysdate,
       |  dt_start    date default sysdate,
       |  message     varchar2(4000)
       |)
     """.stripMargin

  def createPKJournal =
    s"alter table $journalTableName add constraint PK_$journalTableName primary key (process_id, code)"

  def createTableHistory =
    s"""
       |create table $historyTableName
       |(
       |  process_id  varchar2(100),
       |  fileName    varchar2(1000),
       |  ip          varchar2(30),
       |  dt          date default sysdate,
       |  codeArray   varchar2(500)
       |)
     """.stripMargin

  def createPKHistory =
    s"alter table $historyTableName add constraint pk_$historyTableName primary key (process_id)"

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
    s"select code, to_char(dt_start,'DD.MM.YYYY HH24:MI:SS') as dt_start, to_char(dt,'DD.MM.YYYY HH24:MI:SS') as dt, message from $journalTableName where process_id = ?"

  def queryHistory =
    s"select process_id, fileName, ip, dt, codeArray from $historyTableName"

  def queryHistoryById =
    s"select process_id, fileName, ip, dt, codeArray from $historyTableName where process_id = ?"


  def messageHistory =
    s"""
       |insert into $historyTableName
       |(process_id, fileName, ip, dt, codeArray)
       |values
       |(?,?,?,?,?)
     """.stripMargin

}
