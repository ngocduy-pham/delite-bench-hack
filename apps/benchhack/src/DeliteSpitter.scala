object DeliteSpitter extends Spitter {
  import Constant._

  val scalameterName = scalameterDeliteLocUnoptimized
  val benchmarkOut = outDeliteLocUnoptimized
  val approach = "Delite_unoptimized"

  def name(id: Int): String = ""
  def startFile(id: Int): String = ""

  def measuring(toBench: List[String]): String =
    toBench map (sample => s"""
      measure method "$sample" in {
        using(runs) in {
          loop => for (_ <- 1 to loop) {
            val delite = new $sample()
            delite main Array("false")
          }
        }
      }""") mkString ""

  def interval(id: Int): String = ""

  def main(args: Array[String]): Unit = {
    val optimizedSpitter = new OptigraphOptimizedSpitter
    val benchmarks = optimizedSpitter.spit()
    optimizedSpitter.emitScalameter(benchmarks)
    emitScalameter(benchmarks)
  }

}
