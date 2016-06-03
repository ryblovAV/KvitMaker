package engine

import java.util.Date

import config.AppConfig
import engine.db.{DBLogWriter, DBReader}
import engine.file.FileEngine
import play.Logger._
import services.parameters.{CisDivision, MkdChs}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

object ExportEngine {


  def startPartition(processId: String,
                     codeArray: Array[String],
                     dt: Date,
                     mkdChs: MkdChs,
                     cisDivision: CisDivision,
                     dbLogWriter: DBLogWriter,
                     removeFromActiveKey: String => Unit
                    ) = {
    Future {
      info(s"start partition codeArray:${codeArray.mkString(";)}")
      codeArray.map(code =>
        DBReader.readBillsFromDb(
          dt,mkdChs,cisDivision,code,dbLogWriter,removeFromActiveKey)
        .map(bills => GroupEngine.run(bills))
        .map(groupBills => FileEngine.makeAllFile(
                processId,
                mkdChs,
                cisDivision,
                code,
                dt,
                groupBills.map(_.asJava).asJava))
        )
    }
  }


  def start(processId: String,
            mkdPremiseId: String,
            dt: Date,
            mkdChs: MkdChs,
            cisDivision: CisDivision,
            codeArray: Array[String],
            dbLogWriter: DBLogWriter,
            removeFromActiveKey: String => Unit
           ) = {



    val n = if (codeArray.size < AppConfig.countPartition) 1 else codeArray.size / AppConfig.countPartition

    info(s"codeArray.size = ${codeArray.size}, countPartition = ${AppConfig.countPartition}, n = $n")

    val l = codeArray
      .grouped(n)
      .map(
        codeArray => startPartition(
          processId,
          codeArray,
          dt,
          mkdChs,
          cisDivision,
          dbLogWriter,
          removeFromActiveKey)
    )

    Future.sequence(l.toList)
  }

}
