class OptigraphOptimizedSpitter extends Spitter {
  import Constant._

  val scalameterName = scalameterDeliteLocOptimized
  val benchmarkOut = outDeliteLocOptimized
  val approach = "Delite_optimized"

  def name(id: Int): String = s"$benchDeliteLOC${id}0"

  def startFile(id: Int): String = s"""
    import ppl.dsl.optigraph._
    class ${name(id)} extends OptiGraphApplicationRunner {
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

  def measuring(toBench: List[String]): String =
    toBench map (sample => s"""
      measure method "$sample" in {
        using(runs) in {
          loop => for (_ <- 1 to loop) {
            val delite = new $sample()
            delite main Array("true")
            val current = delite.constBuff
            if (previous == null) {
              println("init")
              previous = new scala.collection.mutable.ArrayBuffer[Any]() ++ current
            }
            else if (current != previous) {
              println("recompile")
              previous.clear()
              previous ++= current
            }
            else {
              println("retrieve :)")
            }
          }
        }
      }""") mkString ""

}
