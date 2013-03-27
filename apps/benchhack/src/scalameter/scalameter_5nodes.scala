import java.io.{ FileWriter, PrintWriter, BufferedWriter }
import org.scalameter.api._
import org.scalameter.CurveData
import org.scalameter.Context
import org.scalameter.Executor.Measurer
import org.scalameter.utils.Tree

object scalameter_5nodes extends PerformanceTest {
  import Constant._

  lazy val executor = SeparateJvmsExecutor(
    Executor.Warmer.Default(),
    Aggregator.average,
    new Measurer.Default with Measurer.OutlierElimination)

  lazy val reporter = new Reporter {

    def report(result: CurveData, persistor: Persistor) {
      val stream = new PrintWriter(new BufferedWriter(new FileWriter(out5Nodes, true)))
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

  var previous: scala.collection.mutable.ArrayBuffer[Any] = null

  performance of "5 nodes applications" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1
  ) in {

      measure method " Delite optimized" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop) {

              val delite = new bench_delite_5nodes
              delite main Array("true")
              val current = delite.constBuff
              if (previous == null) {
                previous = new scala.collection.mutable.ArrayBuffer[Any]() ++ current
              }
              else if (current != previous) {
                previous.clear()
                previous ++= current
              }
              else {}
            }
        }
      }

      measure method "Delite unoptimized" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop) {
              val delite = new bench_delite_5nodes
              delite main Array("false")
            }
        }
      }

      measure method "YinYang" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop)
              YYStorage.checkRef[Any](1, Seq(FreeValueContainer.s1, FreeValueContainer.s2), Seq(), () => ())
        }
      }
    }

}
