import java.io.{ File => JFile }

trait Spitter {
  import Constant._

  val scalameterName: String
  val approach: String

  def name(id: Int): String
  def startFile(id: Int): String
  def interval(id: Int): String
  val endFile = """
      }
    }
  """

  def measuring(toBench: List[String]): String

  val benchmarkOut: String

  def scalameter(measuring: String): String = s"""
    import java.io.{ FileWriter, File, PrintWriter, BufferedWriter }

    import org.scalameter.api._
    import org.scalameter.CurveData
    import org.scalameter.Context
    import org.scalameter.Log
    import org.scalameter.Executor.Measurer
    import org.scalameter.utils.Tree

    import ppl.dsl.optigraph._

    object $scalameterName extends PerformanceTest {

      lazy val executor = SeparateJvmsExecutor(
        Executor.Warmer.Default(),
        Aggregator.average,
        new Measurer.Default with Measurer.OutlierElimination)

      lazy val reporter = new Reporter {

        def report(result: CurveData, persistor: Persistor) {
          val stream = new PrintWriter(new BufferedWriter(new FileWriter("$benchmarkOut", true)))
          // output context
          println(s"::Benchmark $${result.context.scope}::")
          //stream.println(s"::Benchmark $${result.context.scope}::")
          for ((key, value) <- result.context.properties.filterKeys(Context.machine.properties.keySet.contains).toSeq.sortBy(_._1)) {
            println(s"$$key: $$value")
          }

          // output measurements
          for (measurement <- result.measurements) {
            println(s"$${measurement.params}: $${measurement.time}")
            stream.println(s"$${measurement.time}")
          }

          println("")
          stream.close()
        }

        def report(result: Tree[CurveData], persistor: Persistor) = true

      }

      lazy val persistor = Persistor.None

      val runs = Gen.single("runs")(1)

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
