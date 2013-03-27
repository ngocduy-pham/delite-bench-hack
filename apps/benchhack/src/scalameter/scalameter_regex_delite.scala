import org.scalameter.api.exec
import org.scalameter.api.machine

object Input {
  var s = ""
  val elem = "i"
  val pattern = "i*"
}

class Regexer extends ndp.RegexApplicationRunner with ndp.RegexApplication /* with scala.virtualization.lms.common.IfThenElseFatExp*/ {
  def main() {
    if (matches(Input.s, Input.pattern)) println("Success!")
  }
}

object scalameter_regex_delite_guard extends Scalameter {

  val outputFile = Constant.outRegexDeliteGuard
  val outputFormat = s"""
  regex benchmark, delite-optimized guard in $outputFile.
  - 1-length string
  - 401-length string
    ...
  """

  var previous: scala.collection.mutable.ArrayBuffer[Any] = null

  performance of "Delite" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1) in {

      for (mult <- 1 to 500 by 20) {

        measure method "guard" in {
          using(runs) setUp (_ => { Input.s = Input.elem * mult * mult; println(s"String length: ${Input.s.length()}") }) in {
            loop =>
              for (_ <- 1 to loop) {
                val delite = new Regexer
                delite main Array("true")
                val current = delite.constBuff
                if (previous == null) {
                  previous = new scala.collection.mutable.ArrayBuffer[Any]() ++ current
                }
                else if (current != previous) {
                  //println(":(")
                  previous.clear()
                  previous ++= current
                }
                else {
                  /*println(s"***** ${current.size}")
                  println(":)")*/
                }
              }
          }
        }

        measure method " Run rightaway" in {
          using(runs) setUp (_ => Input.s = Input.elem * mult * mult) in {
            loop ⇒
              for (_ <- 1 to loop) {
                //val s = text.map(x => (x + 1).toChar)
                val ok = Input.s matches Input.pattern
                //if (ok) println()
              }
          }
        }

      }
    }

}

object scalameter_regex_delite_computation extends Scalameter {

  val outputFile = Constant.outRegexDeliteComputation
  val outputFormat = s"""
  regex benchmark, matching computation in $outputFile.
  - 1-length string
  - 401-length string
    ...
  """
  performance of "Delite.regex" config (
    exec.benchRuns -> 3,
    exec.minWarmupRuns -> 5,
    exec.maxWarmupRuns -> 10,
    machine.cores -> 2,
    exec.independentSamples -> 1) in {

      for (mult <- 1 to 500 by 20) {

        measure method " Run rightaway" in {
          using(runs) setUp (_ => Input.s = Input.elem * mult * mult) in {
            loop ⇒
              for (_ <- 1 to loop) {
                //val s = text.map(x => (x + 1).toChar)
                val ok = Input.s matches Input.pattern
                //if (ok) println()
              }
          }
        }

      }
    }

}
