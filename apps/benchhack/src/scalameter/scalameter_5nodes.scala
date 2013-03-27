import org.scalameter.api.exec
import org.scalameter.api.machine

object scalameter_5nodes extends Scalameter {

  val outputFile = Constant.out5Nodes
  val outputFormat = s"""
  output format for 5 inode benchmark in $outputFile:
  - delite-optimized
  - delite-unoptized
  - yinyang
  """

  var previous: scala.collection.mutable.ArrayBuffer[Any] = null

  performance of "5 nodes applications" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1
  ) in {

      measure method " Delite optimized" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop) {

              val delite = new bench_delite_5nodes
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

      measure method "Delite unoptimized" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop) {
              val delite = new bench_delite_5nodes
              delite main Array("false")
            }
        }
      }

      measure method "YinYang" in {
        using(runs) in {
          loop =>
            for (_ <- 1 to loop)
              YYStorage.checkRef[Any](1, Seq(FreeValueContainer.s1, FreeValueContainer.s2), Seq(), () => ())
        }
      }
    }

}
