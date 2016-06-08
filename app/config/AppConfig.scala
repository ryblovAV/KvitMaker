package config

import java.text.SimpleDateFormat

object AppConfig {
  val countPartition = 8
  val partition = 1000

  val templatePath = s"$root//conf//templates//template.xlsx"

  val COMPANY_TAG = "<COMPANY>"
  val MKD_TAG = "<IS_MKD>"
  val BODY_TAG = "<BODY>"
  val MERGE_TAG = "<MERGE>"
  val COUNT_ALL_TAG = "<COUNT_ALL>"

  val df = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy")

}
