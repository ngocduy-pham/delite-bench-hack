import java.io.{ FileWriter, PrintWriter, BufferedWriter }
import org.scalameter.api._
import org.scalameter.CurveData
import org.scalameter.Context
import org.scalameter.Executor.Measurer
import org.scalameter.utils.Tree
import ppl.dsl.optigraph.OptiGraphApplicationRunner

object scalameter_realLife extends PerformanceTest {
  import Constant._

  lazy val executor = SeparateJvmsExecutor(
    Executor.Warmer.Default(),
    Aggregator.average,
    new Measurer.Default with Measurer.OutlierElimination)

  lazy val reporter = new Reporter {

    def report(result: CurveData, persistor: Persistor) {
      val stream = new PrintWriter(new BufferedWriter(new FileWriter(outRealLife, true)))
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

  val newSCC: () => OptiGraphApplicationRunner = () => new SCC
  val newPRank: () => OptiGraphApplicationRunner = () => new PRank
  val newCond: () => OptiGraphApplicationRunner = () => new Cond

  performance of "Real life applications" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1
  ) in {

      measure method " Delite optimized" in {

        for (newApp <- Seq(newSCC, newPRank, newCond)) {

          using(runs) in {
            loop =>
              for (_ <- 1 to loop) {
                val delite = newApp()
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

      }

      measure method "Delite unoptimized" in {

        for (newApp <- Seq(newSCC, newPRank, newCond)) {

          using(runs) in {
            loop =>
              for (_ <- 1 to loop) {
                val delite = newApp()
                delite main Array("false")
              }
          }

        }

      }

      measure method "YinYang SCC" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop)
              YYStorage.checkRef[Any](1, Seq(FreeValueContainer.x1, FreeValueContainer.x2, FreeValueContainer.x3, FreeValueContainer.x4, FreeValueContainer.x5, FreeValueContainer.x6, FreeValueContainer.x7, FreeValueContainer.x8, FreeValueContainer.s9, FreeValueContainer.s10), Seq(), () => ())
        }
      }

      measure method "YinYang P.Rank" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop)
              YYStorage.checkRef[Any](2, Seq(FreeValueContainer.x1, FreeValueContainer.x85, FreeValueContainer.x6, FreeValueContainer.x4, FreeValueContainer.x7, FreeValueContainer.s1, FreeValueContainer.x11, FreeValueContainer.x8, FreeValueContainer.s9), Seq(), () => ())
        }
      }

      measure method "YinYang P.Rank" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop)
              YYStorage.checkRef[Any](3, Seq(FreeValueContainer.x100, FreeValueContainer.x80, FreeValueContainer.x10, FreeValueContainer.x200, FreeValueContainer.x3, FreeValueContainer.s1, FreeValueContainer.x90, FreeValueContainer.x4, FreeValueContainer.x1, FreeValueContainer.x3, FreeValueContainer.x2, FreeValueContainer.x6), Seq(), () => ())
        }
      }

    }

}
