object OptigraphOptimizedSpitter extends Spitter {

  val scalameterName: String = "optigraphOptimizedScalameter"

  override val benchmarkOut = raw"./optigraphOptimizedBenchmark.benchmark"

  def name(id: Int): String = s"optigraphOptimizedBench$id"

  def startFile(id: Int): String = s"""
    import ppl.dsl.optigraph._
    object ${name(id)} extends OptiGraphApplicationRunner {
      def main() = {
  """

  def interval(id: Int): String = {
    val i = (id - 1) * 5
    s"""
    println($freeContainer.s${i + 1} + $freeContainer.s${i + 2})
    val g$i = Graph()
    val n${i + 1} = g$i.AddNode
    val n${i + 2} = g$i.AddNode
    val x$i = $freeContainer.x${i + 1} + $freeContainer.x${i + 2} + $freeContainer.x${i + 5}
    val y$i = $freeContainer.s${i + 3} + $freeContainer.s${i + 4} + $freeContainer.s${i + 5}
    println(x$i + y$i + $freeContainer.x${i + 3} + $freeContainer.x${i + 4})
    val e${i + 1} = g$i.AddEdge(n${i + 1}, n${i + 2})
    val n${i + 3} = g$i.AddNode
    g$i.AddEdge(n${i + 2}, n${i + 3})
  """
  }

  def measuring(toBench: List[String]): String = s"""
     performance of "Optimized Delite" in {
     ${
    toBench map (sample => s"""
        measure method "$sample" config (
          exec.benchRuns -> 3,
          exec.minWarmupRuns -> 5,
          exec.maxWarmupRuns -> 10,
          machine.cores -> 2,
          exec.independentSamples -> 1
        ) in {
          using(runs) in {
            loop => for (_ <- 1 to loop) $sample main Array("true")
          }
        }         
     """) mkString ""
  }
      
      }
    """

}
