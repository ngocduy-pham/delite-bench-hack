
    import org.scalameter.api.exec
    import org.scalameter.api.machine

    import ppl.dsl.optigraph._

    object scalameter_deliteLOC_unoptimized extends Scalameter {

      val outputFile = "delite_LOC_unoptimized.benchmark"
      val outputFormat = s"""
  output format for LOC benchmark, delite-unoptimized in delite_LOC_unoptimized.benchmark:
  - 500LOC benchmark
  - 1000LOC benchmark
  """
  
      var previous: scala.collection.mutable.ArrayBuffer[Any] = null

      performance of "Delite_unoptimized" config (
        exec.benchRuns -> 3,
        exec.minWarmupRuns -> 5,
        exec.maxWarmupRuns -> 10,
        machine.cores -> 2,
        exec.independentSamples -> 1
      ) in {
        
      measure method "bench_delite_LOC500" in {
        using(runs) in {
          loop => for (_ <- 1 to loop) {
            val delite = new bench_delite_LOC500()
            delite main Array("false")
          }
        }
      }
      measure method "bench_delite_LOC1000" in {
        using(runs) in {
          loop => for (_ <- 1 to loop) {
            val delite = new bench_delite_LOC1000()
            delite main Array("false")
          }
        }
      }
      }

    }

  