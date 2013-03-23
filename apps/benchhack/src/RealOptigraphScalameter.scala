

import java.io.{ FileWriter, File, PrintWriter, BufferedWriter }

import org.scalameter.api._
import org.scalameter.CurveData
import org.scalameter.Context
import org.scalameter.Log
import org.scalameter.Executor.Measurer
import org.scalameter.utils.Tree

import ppl.dsl.optigraph._

object RealOptigraphScalameter extends PerformanceTest {

  lazy val executor = SeparateJvmsExecutor(
    Executor.Warmer.Default(),
    Aggregator.average,
    new Measurer.Default with Measurer.OutlierElimination)

  lazy val reporter = new Reporter {

    def report(result: CurveData, persistor: Persistor) {
      val stream = new PrintWriter(new BufferedWriter(new FileWriter(raw"D:\enjoy\Delite\realOptigraphBenchmark.benchmark", true)))
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

  val runs = Gen.single("runs")(10)

  performance of "RealOptiGraphRunner" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1
  ) in {
      measure method "optimized" in {
        using(runs) in {
          loop => RealOptiGraphRunner main Array("true")
        }
      }
      measure method "unoptimized" in {
        using(runs) in {
          loop => RealOptiGraphRunner main Array("false")
        }
      }
    }

}

  