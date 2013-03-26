object FreeValueCreator {

  def main(args: Array[String]): Unit = {
    val fileName = s"${Constant.generated}FreeValueContainer.scala"
    Utils createFile fileName
    Utils.write(fileName, s"""
      object FreeValueContainer {
        ${
      (for (i <- 1 to 500) yield s"""
          val x$i = $i
          val s$i = "stupid string $i"
          """) mkString ""
    }
      }
    """)
  }

}
