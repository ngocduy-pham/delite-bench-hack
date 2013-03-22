import java.io.{ File => JFile }

trait Spitter {

  def valobjName(id: Int): String
  def valobj(name: String): String = s"""
    object $name {
      val x = 1
      val s = "stupid string"
    }
  """
  def name(id: Int): String
  def startFile(id: Int, valobjName: String): String
  def interval(id: Int, valobj: String): String
  val endFile = """
      }
    }
  """

  def main(args: Array[String]): Unit = spit()

  def spit(): List[JFile] = {
    (for (i <- 1 to 3) yield {
      val fileName = s"${name(i)}.scala"
      val file = Utils createFile fileName
      val valobj = valobjName(i)
      Utils.write(fileName, startFile(i, valobj), true)
      Utils.write(fileName, (for (j <- 1 to i) yield interval(j, valobj)) mkString "")
      Utils.write(fileName, endFile)
      file
    }).toList
  }

}