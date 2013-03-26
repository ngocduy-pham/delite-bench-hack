object YYSpitter extends Spitter {
  import Constant._

  val benchmarkOut = outYYLoc
  val scalameterName = scalameterYYLoc
  val approach = "YinYang"

  def name(id: Int): String = s"mpdeBench$id"
  def startFile(id: Int): String = ""
  def interval(id: Int): String = ""

  def measuring(toBench: List[String]): String = {
    val ids = toBench map (_.toInt)
    val constBuff = ids map (id => {
      s"Seq(${
        (for (step <- 0 until (id / 2) by 5) yield {
          (for (j <- 1 to 5) yield s"$freeContainer.s${step + j}, $freeContainer.x${step + j}") mkString ", "
        }) mkString ", "
      })"
    })

    ids map (id => s"""
      measure method "bench_yy_LOC_$id" in {
        using(runs) in {
          loop => for (_ <- 1 to loop)
            YYStorage.checkRef[Any]($id, ${constBuff(id / 500 - 1)}, Seq(), () => ())
        }
      }""") mkString "\n"
  }

  def main(args: Array[String]): Unit = {
    emitScalameter(List("500", "1000"))
  }

}
