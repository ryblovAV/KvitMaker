package config

import java.io.FileInputStream
import java.util.Properties

import play.Logger._

object DBConfig {

  private val dbProperties = readDbPropertiesFile

  def readDbPropertiesFile = {

    info("read db properties")

    val prop = new Properties()

    val inputStream = new FileInputStream(AppConfig.dbPropertiesPath)
    info(s"inputStream = $inputStream")

    try {
      prop.load(inputStream)
    } finally {
      if (inputStream != null) inputStream.close()
    }

    prop
  }

  val driver = "oracle.jdbc.driver.OracleDriver"
  val url = dbProperties.getProperty("url")
  val username = dbProperties.getProperty("username")
  val password = dbProperties.getProperty("password")

}
