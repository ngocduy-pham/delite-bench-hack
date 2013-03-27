import org.scalameter.api.exec
import org.scalameter.api.machine
import ppl.dsl.optigraph.OptiGraphApplicationRunner

object scalameter_realLife_delite_optimized extends Scalameter {
  import Constant._

  val outputFile = Constant.outRealLifeDeliteOptimized
  val outputFormat = s"""
  output format for real life benchmarks, delite-optimized in $outputFile:
  - SCC
  - Page rank
  - Conductance
  """

  var previous: scala.collection.mutable.ArrayBuffer[Any] = null

  val newSCC: () => OptiGraphApplicationRunner = () => new SCC
  val newPRank: () => OptiGraphApplicationRunner = () => new PRank
  val newCond: () => OptiGraphApplicationRunner = () => new Cond

  performance of "Real life applications" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1
  ) in {

      measure method " Delite optimized" in {

        for (newApp <- Seq(newSCC, newPRank, newCond)) {

          using(runs) in {
            loop =>
              for (_ <- 1 to loop) {
                val delite = newApp()
                delite main Array("true")
                val current = delite.constBuff
                if (previous == null) {
                  previous = new scala.collection.mutable.ArrayBuffer[Any]() ++ current
                }
                else if (current != previous) {
                  previous.clear()
                  previous ++= current
                }
                else {}
              }
          }

        }

      }

    }

}

object scalameter_realLife_delite_unoptimized extends Scalameter {
  import Constant._

  val outputFile = Constant.outRealLifeDeliteUnoptimized
  val outputFormat = s"""
  output format for real life benchmarks, delite-unoptimized in $outputFile:
  - SCC
  - Page rank
  """

  val newSCC: () => OptiGraphApplicationRunner = () => new SCC
  val newPRank: () => OptiGraphApplicationRunner = () => new PRank
  val newCond: () => OptiGraphApplicationRunner = () => new Cond

  performance of "Real life applications" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1
  ) in {

      measure method "Delite unoptimized" in {

        for (newApp <- Seq(newSCC, newPRank /*, newCond*/ )) {

          using(runs) in {
            loop =>
              for (_ <- 1 to loop) {
                val delite = newApp()
                delite main Array("false")
              }
          }

        }

      }

    }
}

object scalameter_realLife_yinyang extends Scalameter {
  import Constant._

  val outputFile = Constant.outRealLifeYY
  val outputFormat = s"""
  output format for real life benchmarks, yinyang in $outputFile:
  - SCC
  - Page rank
  - Conductance
  """

  performance of "Real life applications" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1
  ) in {

      measure method "YinYang SCC" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop)
              YYStorage.checkRef[Any](1, Seq(FreeValueContainer.x1, FreeValueContainer.x2, FreeValueContainer.x3, FreeValueContainer.x4, FreeValueContainer.x5, FreeValueContainer.x6, FreeValueContainer.x7, FreeValueContainer.x8, FreeValueContainer.s9, FreeValueContainer.s10), Seq(), () => ())
        }
      }

      measure method "YinYang P.Rank" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop)
              YYStorage.checkRef[Any](2, Seq(FreeValueContainer.x1, FreeValueContainer.x85, FreeValueContainer.x6, FreeValueContainer.x4, FreeValueContainer.x7, FreeValueContainer.s1, FreeValueContainer.x11, FreeValueContainer.x8, FreeValueContainer.s9), Seq(), () => ())
        }
      }

      measure method "YinYang P.Rank" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop)
              YYStorage.checkRef[Any](3, Seq(FreeValueContainer.x100, FreeValueContainer.x80, FreeValueContainer.x10, FreeValueContainer.x200, FreeValueContainer.x3, FreeValueContainer.s1, FreeValueContainer.x90, FreeValueContainer.x4, FreeValueContainer.x1, FreeValueContainer.x3, FreeValueContainer.x2, FreeValueContainer.x6), Seq(), () => ())
        }
      }

    }

}
