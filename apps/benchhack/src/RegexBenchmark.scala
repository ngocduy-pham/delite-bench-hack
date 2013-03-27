import java.io.FileWriter
import java.io.BufferedWriter
import java.io.PrintWriter

import org.scalameter.api.SeparateJvmsExecutor
import org.scalameter.api.Executor
import org.scalameter.api.Aggregator
import org.scalameter.api.PerformanceTest
import org.scalameter.api.Measurer
import org.scalameter.api.Reporter
import org.scalameter.api.Persistor
import org.scalameter.api.Gen
import org.scalameter.api.exec
import org.scalameter.api.machine
import org.scalameter.CurveData
import org.scalameter.utils.Tree
import org.scalameter.Context

object Input {
  var s = ""
  val elem = "i"
  val pattern = "i*"
}

class Regexer extends ndp.RegexApplicationRunner with ndp.RegexApplication/* with scala.virtualization.lms.common.IfThenElseFatExp*/ {
  def main() {
    if (matches(Input.s, Input.pattern)) println("Success!")
  }
}

object RegexBenchmark extends PerformanceTest {
  import Constant._

  lazy val executor = SeparateJvmsExecutor(
    Executor.Warmer.Default(),
    Aggregator.average,
    new Measurer.Default with Measurer.OutlierElimination)

  lazy val reporter = new Reporter {

    def report(result: CurveData, persistor: Persistor) {
      val stream = new PrintWriter(new BufferedWriter(new FileWriter(outRegexDelite, true)))
      // output context
      println(s"::Benchmark ${result.context.scope}::")
      //stream.println(s"::Benchmark ${result.context.scope}::")
      for ((key, value) ← result.context.properties.filterKeys(Context.machine.properties.keySet.contains).toSeq.sortBy(_._1)) {
        println(s"$key: $value")
        //stream.println(s"$key: $value")
      }

      // output measurements
      for (measurement ← result.measurements) {
        println(s"${measurement.params}: ${measurement.time}")
        stream.println(s"${measurement.time}")
      }

      // add a new line
      println("")
      stream.close()
    }

    def report(result: Tree[CurveData], persistor: Persistor) = true

  }

  lazy val persistor = Persistor.None

  val runs = Gen.single("runs")(1)

  var previous: scala.collection.mutable.ArrayBuffer[Any] = null

  performance of "Delite" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1) in {

      for (mult <- 520 to 1000 by 20) {

        measure method "regex" in {
          using(runs) setUp (_ => { Input.s = Input.elem * mult * mult; println(s"String length: ${Input.s.length()}") }) in {
            loop =>
              for (_ <- 1 to loop) {
                val delite = new Regexer
                delite main Array("true")
                val current = delite.constBuff
                if (previous == null) {
                  previous = new scala.collection.mutable.ArrayBuffer[Any]() ++ current
                }
                else if (current != previous) {
                  //println(":(")
                  previous.clear()
                  previous ++= current
                }
                else {
                  /*println(s"***** ${current.size}")
                  println(":)")*/
                }
              }
          }
        }

        measure method "run rightaway" in {
          using(runs) setUp (_ => Input.s = Input.elem * mult * mult) in {
            loop ⇒
              for (_ <- 1 to loop) {
                val ok = Input.s matches Input.pattern
                if (ok) println()
              }
          }
        }

      }
    }

}
