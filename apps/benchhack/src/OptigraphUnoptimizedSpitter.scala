object OptigraphUnoptimizedSpitter extends Spitter {

  val scalameterName: String = "optigraphUnoptimizedScalameter"

  override val benchmarkOut = "raw\"D:\\enjoy\\Delite\\optigraphUnptimizedBenchmark.benchmark\""

  def name(id: Int): String = s"optigraphUnoptimizedBench$id"

  def startFile(id: Int): String = s"""
    import ppl.dsl.optigraph._
    object ${name(id)} extends OptiGraphApplicationRunner {
      def main() = {
  """

  def interval(id: Int): String = OptigraphOptimizedSpitter.interval(id)

  def measuring(toBench: List[String]): String = s"""
     performance of "Unoptimized Delite" in {
     ${
    toBench map (sample => s"""
        measure method "$sample" in {
          using(runs) in {
            loop => for (_ <- 1 to loop) $sample main Array("false")
          }
        }         
     """) mkString ""
  }
      
      }
    """

}
