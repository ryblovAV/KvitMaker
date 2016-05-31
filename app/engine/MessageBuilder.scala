package engine

object MessageBuilder {

  def repeatCodeMessage(code: Array[String]) =
    s"Операция не была выполнена, так как по участкам: ${code.mkString(";")}, уже выполняется процесс формирования квитанций."

}
