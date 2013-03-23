import java.io.{ FileWriter, File, PrintWriter, BufferedWriter }

import org.scalameter.api._
import org.scalameter.CurveData
import org.scalameter.Context
import org.scalameter.Log
import org.scalameter.Executor.Measurer
import org.scalameter.utils.Tree

import ppl.dsl.optigraph._

object Outside {
  val x = 0
  val m = "hello"
}

object Real extends OptiGraphApplicationRunner with OptiGraphReal

trait OptiGraphReal extends OptiGraphApplication {
  def main() {
    println(Outside.m)
    val g = Graph()
    val n1 = g.AddNode
    val n2 = g.AddNode
    val x = 3
    val y = Outside.x + 1
    println(x + y)
    val e1 = g.AddEdge(n1, n2)
    val n3 = g.AddNode
    g.AddEdge(n2, n3)
  }
}

object Benchmark extends PerformanceTest {

  lazy val executor = SeparateJvmsExecutor(
    Executor.Warmer.Default(),
    Aggregator.average,
    new Measurer.Default with Measurer.OutlierElimination
  )

  lazy val reporter = new Reporter {

    def report(result: CurveData, persistor: Persistor) {
      val stream = new PrintWriter(new BufferedWriter(new FileWriter(raw"D:\enjoy\Delite\benchmark.out", true)))
      // output context
      println(s"::Benchmark ${result.context.scope}::")
      //stream.println(s"::Benchmark ${result.context.scope}::")
      for ((key, value) <- result.context.properties.filterKeys(Context.machine.properties.keySet.contains).toSeq.sortBy(_._1)) {
        println(s"$key: $value")
        //stream.println(s"$key: $value")
      }

      // output measurements
      for (measurement <- result.measurements) {
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

  performance of "Yingyang approach" in {

    measure method "Real" in {
      using(runs) in {
        loop => for (_ <- 1 to loop) CompiledStorage.check("Real", List(Outside.m, Outside.x))
      }

    }
  }

}
