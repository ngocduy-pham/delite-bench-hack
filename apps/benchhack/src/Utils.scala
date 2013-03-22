import java.io.{ File => JFile }
import java.io.FileWriter

object Utils {

  def write(filename: String, message: String, overwrite: Boolean = false) {
    val writer = new FileWriter(filename, !overwrite)
    writer write message
    //writer write (System getProperty "line.separator")
    writer.close()
  }

  def createFile(filename: String): JFile = {
    val file = new JFile(filename)
    file.createNewFile()
    file
  }

}