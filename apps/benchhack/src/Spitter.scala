import java.io.{ File => JFile }

trait Spitter {
  import Constant._

  val scalameterName: String
  val approach: String
  val outputFile: String
  val outputFormat: String

  def name(id: Int): String
  def startFile(id: Int): String
  def interval(id: Int): String
  val endFile = """
      }
    }
  """

  def measuring(toBench: List[String]): String

  def scalameter(measuring: String): String = s"""
    import org.scalameter.api.exec
    import org.scalameter.api.machine

    import ppl.dsl.optigraph._

    object $scalameterName extends Scalameter {

      val outputFile = "$outputFile"
      val outputFormat = s${"\"\"\"" + outputFormat + "\"\"\""}
  
      var previous: scala.collection.mutable.ArrayBuffer[Any] = null

      performance of "$approach" config (
        exec.benchRuns -> 3,
        exec.minWarmupRuns -> 5,
        exec.maxWarmupRuns -> 10,
        machine.cores -> 2,
        exec.independentSamples -> 1
      ) in {
        ${measuring}
      }

    }

  """

  def emitScalameter(benchmarks: List[String]) {
    val benchmarker = scalameter(measuring(benchmarks))
    val fileName = s"$generated$scalameterName.scala"
    val file = Utils createFile fileName
    Utils.write(fileName, benchmarker, true)
  }

  def spit(): List[String] = {
    (for (i <- 50 to 100 by 50) yield {
      val objName = name(i)
      val fileName = s"$generated$objName.scala"
      val file = Utils createFile fileName
      Utils.write(fileName, startFile(i), true)
      Utils.write(fileName, (for (j <- 1 to i) yield interval(j)) mkString "")
      Utils.write(fileName, endFile)
      objName
    }).toList
  }

}
