import ppl.dsl.optigraph._
import org.scalameter.api._

object Outside {
  val x = 0
  val m = "hello"
}

object Hello extends OptiGraphApplicationRunner with OptiGraphHello

trait OptiGraphHello extends OptiGraphApplication {
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

  lazy val executor = SeparateJvmsExecutor(Executor.Warmer.Default(), Aggregator.average, new Measurer.Default)

  lazy val reporter = new LoggingReporter

  lazy val persistor = Persistor.None

  val sizes = Gen.single("shit")(1)

  performance of "Delite application" in {
    measure method " non-optimized" in {
      using(sizes) in {
        _ => Hello main Array("true")
      }
    }
  }
  
}
/*
object BenchmarkNonOptimized extends Benchmark {
  performance of "Delite application" in {
    measure method " non-optimized" in {
      using(sizes) in {
        _ => Hello main Array("false")
      }
    }
  }
}

object BenchmarkOptimized extends Benchmark {
  performance of "Delite application" in {
    measure method " optimized" in {
      using(sizes) in {
        _ => Hello main Array("true")
      }
    }
  }
}
*/