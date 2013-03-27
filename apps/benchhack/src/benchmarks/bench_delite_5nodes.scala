import ppl.dsl.optigraph._

class bench_delite_5nodes extends OptiGraphApplicationRunner {
  def main() = {
    println(FreeValueContainer.s1 + FreeValueContainer.s2)
    val g0 = Graph()
    val n1 = g0.AddNode
    val n2 = g0.AddNode
    g0.AddEdge(n1, n2)
  }
}
