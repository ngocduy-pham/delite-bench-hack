object MPDESpitter extends Spitter {

  override val benchmarkOut = "raw\"D:\\enjoy\\Delite\\mpdeBenchmark.benchmark\\\""

  val scalameterName: String = "mpdeScalameter"

  def name(id: Int): String = s"mpdeBench$id"

  def startFile(id: Int): String = {
    val objName = name(id)
    currentObj = objName
    constBuff.update(currentObj, "List[Any]()")

    s"""
    import ppl.dsl.optigraph._
     object $objName extends OptiGraphApplicationRunner {
      def main() = {
  """
  }

  def interval(id: Int): String = {
    val i = (id - 1) * 5
    val currentList = constBuff(currentObj)
    constBuff.update(currentObj, currentList + s" ++ List(${(for (j <- 1 to 5) yield s"$freeContainer.s${i + j}, $freeContainer.x${i + j}") mkString ", "})")

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
    performance of "Yinyang approach" config (
      exec.benchRuns -> 3,
      exec.minWarmupRuns -> 5,
      exec.maxWarmupRuns -> 10,
      machine.cores -> 2,
      exec.independentSamples -> 1
    ) in {
    ${
    toBench map (sample => s"""
        measure method "$sample" in {
          using(runs) in {
            loop => for (_ <- 1 to loop) CompiledStorage.check("$sample", ${constBuff(sample)})
          }
        }         
     """) mkString ""
  }
      
      }
    """

  private var currentObj = ""
  private val constBuff = scala.collection.mutable.WeakHashMap[String, String]()

}
