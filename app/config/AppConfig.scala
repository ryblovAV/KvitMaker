package config

import java.text.SimpleDateFormat
import play.Logger._

object AppConfig {

  val root = System.getProperty("user.dir")

  val countPartition = 8
  val partition = 1000

  val scriptPath   = s"$root//conf//queries//script.sql"
  val templatePath = s"$root//conf//templates//template.xlsx"
  val unionTemplatePath = s"$root//conf//templates//template_all.xlsx"

  val dbPropertiesPath = s"$root//conf//db.properties"

  val COMPANY_TAG = "<COMPANY>"
  val MKD_TAG = "<IS_MKD>"
  val BODY_TAG = "<BODY>"
  val MERGE_TAG = "<MERGE>"
  val COUNT_ALL_TAG = "<COUNT_ALL>"
  val PACKAGES_TAG = "<PACKAGES>"
  val COUNT_BILLS_TAG = "<COUNT_BILLS>"

  val df = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy")



}
