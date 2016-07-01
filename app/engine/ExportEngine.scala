package engine

import java.util.{Date, ArrayList => JArrayList}

import config.AppConfig
import engine.db.{DBLogWriter, DBReader}
import engine.file.FileEngine
import play.Logger._
import services.parameters.{CisDivision, MkdChs}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.Try

object ExportEngine {


  def startPartition(processId: String,
                     read: String => Try[List[JArrayList[String]]],
                     codeArray: Array[String],
                     dt: Date,
                     mkdChs: MkdChs,
                     cisDivision: CisDivision,
                     createPath: (String) => String,
                     createCSVFileName: (String, Int) => String): Future[Array[Try[List[String]]]] = {
    Future {
      info(s"start partition codeArray:${codeArray.mkString(";")}")
      codeArray.map(code =>
        read(code)
          .map(bills => GroupEngine.run(bills))
          .map(groupBills =>
            FileEngine.makeAllFile(
              processId,
              mkdChs,
              code,
              dt,
              createPath,
              createCSVFileName,
              groupBills)
          )
      )
    }
  }

  def start(processId: String,
            mkdPremiseId: String,
            dt: Date,
            mkdChs: MkdChs,
            cisDivision: CisDivision,
            orderByIndex: Boolean,
            codeArray: Array[String],
            dbLogWriter: DBLogWriter,
            removeFromActiveKey: String => Unit
           ) = {

    def readByCode(dt: Date,
                   mkdChs: MkdChs,
                   cisDivision: CisDivision,
                   orderByIndex: Boolean,
                   dbLogWriter: DBLogWriter,
                   removeFromActiveKey: String => Unit)
                  (code: String): Try[List[JArrayList[String]]] = {
      DBReader.readBillsFromDb(dt, mkdChs, cisDivision, code, orderByIndex, dbLogWriter, removeFromActiveKey)
    }

    def readByPremiseId(dt: Date,
                        mkdPremiseId: String,
                        orderByIndex: Boolean,
                        removeFromActiveKey: String => Unit)
                       (code: String): Try[List[JArrayList[String]]] = {
      DBReader.readBillsFromDb(
        dt = dt,
        mkdPremiseId = mkdPremiseId,
        code = code,
        orderByIndex,
        removeFromActiveKey = removeFromActiveKey)
    }

    val n = if (codeArray.size < AppConfig.countPartition) 1 else codeArray.size / AppConfig.countPartition

    info(s"codeArray.size = ${codeArray.size}, countPartition = ${AppConfig.countPartition}, n = $n")
    info(s"codeArray = $codeArray")
    info(s"mkdPremiseId = $mkdPremiseId")

    val l = codeArray
      .grouped(n)
      .map(
        codeArray => startPartition(
          processId = processId,
          read = if (!mkdPremiseId.isEmpty) readByPremiseId(dt, mkdPremiseId, orderByIndex, removeFromActiveKey) else readByCode(dt, mkdChs, cisDivision, orderByIndex, dbLogWriter, removeFromActiveKey),
          codeArray = codeArray,
          dt = dt,
          mkdChs = mkdChs,
          cisDivision = cisDivision,
          createPath = if (!mkdPremiseId.isEmpty) FileNameBuilder.createPremisePath(processId, mkdChs, cisDivision) else FileNameBuilder.createPath(processId, mkdChs, cisDivision),
          createCSVFileName = if (!mkdPremiseId.isEmpty) FileNameBuilder.createPremiseCSVFileName(mkdChs) else FileNameBuilder.createCSVFileName(mkdChs)
        )
      )

    Future.sequence(l.toList)
  }

}
