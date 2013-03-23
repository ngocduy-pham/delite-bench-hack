object FreeValueCreator {

  val benchhack = raw"D:\enjoy\Delite\apps\benchhack\src\generated\"

  def main(args: Array[String]): Unit = {
    val fileName = s"${benchhack}FreeValueContainer.scala"
    Utils createFile fileName
    Utils.write(fileName, s"""
      object FreeValueContainer {
        ${
      (for (i <- 1 to 100) yield s"""
          val x$i = $i
          val s$i = "stupid string $i"
          """) mkString ""
    }
      }
    """)
  }

}
