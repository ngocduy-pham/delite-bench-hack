import java.io.{ FileWriter, File, PrintWriter, BufferedWriter }

import org.scalameter.api._
import org.scalameter.CurveData
import org.scalameter.Context
import org.scalameter.Log
import org.scalameter.Executor.Measurer
import org.scalameter.utils.Tree

object RealMPDEScalameter extends PerformanceTest {

  lazy val executor = SeparateJvmsExecutor(
    Executor.Warmer.Default(),
    Aggregator.average,
    new Measurer.Default with Measurer.OutlierElimination)

  lazy val reporter = new Reporter {

    def report(result: CurveData, persistor: Persistor) {
      val stream = new PrintWriter(new BufferedWriter(new FileWriter(raw"./realMPDEBenchmark.benchmark/", true)))
      // output context
      println(s"::Benchmark ${result.context.scope}::")
      //stream.println(s"::Benchmark ${result.context.scope}::")
      for ((key, value) <- result.context.properties.filterKeys(Context.machine.properties.keySet.contains).toSeq.sortBy(_._1)) {
        println(s"$key: $value")
      }

      // output measurements
      for (measurement <- result.measurements) {
        println(s"${measurement.params}: ${measurement.time}")
        stream.println(s"${measurement.time}")
      }

      println("")
      stream.close()
    }

    def report(result: Tree[CurveData], persistor: Persistor) = true

  }

  lazy val persistor = Persistor.None

  val runs = Gen.single("runs")(1)

  performance of "Yinyang approach" config (
    exec.benchRuns -> 10,
    exec.minWarmupRuns -> 10,
    //exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1
  ) in {

      val dummy: () => Any = () => ()
      measure method "Real MPDE new" in {
        using(runs) in {
          loop => for (_ <- 1 to loop) YYStorage.checkRef[Any](1, Seq(FreeValueContainer.s1, FreeValueContainer.x1), Seq(), dummy)
        }
      }

      val text = "I" * 5000
      val pattern = "I*"
      measure method "Real MPDE new" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop) {
              val s = text.map(x => (x + 1).toChar)
              if (s.matches(pattern)) println("Success!")
            }
        }
      }

    }

}
  