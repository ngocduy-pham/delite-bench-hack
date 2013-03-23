
    import java.io.{ FileWriter, File, PrintWriter, BufferedWriter }

    import org.scalameter.api._
    import org.scalameter.CurveData
    import org.scalameter.Context
    import org.scalameter.Log
    import org.scalameter.Executor.Measurer
    import org.scalameter.utils.Tree

    import ppl.dsl.optigraph._

    object RealMPDEScalameter extends PerformanceTest {

      lazy val executor = SeparateJvmsExecutor(
        Executor.Warmer.Default(),
        Aggregator.average,
        new Measurer.Default with Measurer.OutlierElimination)

      lazy val reporter = new Reporter {

        def report(result: CurveData, persistor: Persistor) {
          val stream = new PrintWriter(new BufferedWriter(new FileWriter(raw"D:\enjoy\Delite\realMPDEBenchmark.benchmark\", true)))
          // output context
          println(s"::Benchmark ${result.context.scope}::")
          //stream.println(s"::Benchmark ${result.context.scope}::")
          for ((key, value) <- result.context.properties.filterKeys(Context.machine.properties.keySet.contains).toSeq.sortBy(_._1)) {
            println(s"$key: $value")
          }

          // output measurements
          for (measurement <- result.measurements) {
            println(s"${measurement.params}: ${measurement.time}")
            stream.println(s"${measurement.time}")
          }

          println("")
          stream.close()
        }

        def report(result: Tree[CurveData], persistor: Persistor) = true

      }

      lazy val persistor = Persistor.None

      val runs = Gen.single("runs")(1)

      
    performance of "Yinyang approach" config (
      exec.benchRuns -> 3,
      exec.minWarmupRuns -> 5,
      exec.maxWarmupRuns -> 10,
      machine.cores -> 2,
      exec.independentSamples -> 1
    ) in {
     
        measure method "Real MPDE" in {
          using(runs) in {
            loop => for (_ <- 1 to loop) CompiledStorage.check("mpdeBench20", List[Any]() ++ List(FreeValueContainer.s1, FreeValueContainer.x1, FreeValueContainer.s2, FreeValueContainer.x2, FreeValueContainer.s3, FreeValueContainer.x3, FreeValueContainer.s4, FreeValueContainer.x4, FreeValueContainer.s5, FreeValueContainer.x5) ++ List(FreeValueContainer.s6, FreeValueContainer.x6, FreeValueContainer.s7, FreeValueContainer.x7, FreeValueContainer.s8, FreeValueContainer.x8, FreeValueContainer.s9, FreeValueContainer.x9, FreeValueContainer.s10, FreeValueContainer.x10) ++ List(FreeValueContainer.s11, FreeValueContainer.x11, FreeValueContainer.s12, FreeValueContainer.x12, FreeValueContainer.s13, FreeValueContainer.x13, FreeValueContainer.s14, FreeValueContainer.x14, FreeValueContainer.s15, FreeValueContainer.x15) ++ List(FreeValueContainer.s16, FreeValueContainer.x16, FreeValueContainer.s17, FreeValueContainer.x17, FreeValueContainer.s18, FreeValueContainer.x18, FreeValueContainer.s19, FreeValueContainer.x19, FreeValueContainer.s20, FreeValueContainer.x20) ++ List(FreeValueContainer.s21, FreeValueContainer.x21, FreeValueContainer.s22, FreeValueContainer.x22, FreeValueContainer.s23, FreeValueContainer.x23, FreeValueContainer.s24, FreeValueContainer.x24, FreeValueContainer.s25, FreeValueContainer.x25) ++ List(FreeValueContainer.s26, FreeValueContainer.x26, FreeValueContainer.s27, FreeValueContainer.x27, FreeValueContainer.s28, FreeValueContainer.x28, FreeValueContainer.s29, FreeValueContainer.x29, FreeValueContainer.s30, FreeValueContainer.x30) ++ List(FreeValueContainer.s31, FreeValueContainer.x31, FreeValueContainer.s32, FreeValueContainer.x32, FreeValueContainer.s33, FreeValueContainer.x33, FreeValueContainer.s34, FreeValueContainer.x34, FreeValueContainer.s35, FreeValueContainer.x35) ++ List(FreeValueContainer.s36, FreeValueContainer.x36, FreeValueContainer.s37, FreeValueContainer.x37, FreeValueContainer.s38, FreeValueContainer.x38, FreeValueContainer.s39, FreeValueContainer.x39, FreeValueContainer.s40, FreeValueContainer.x40) ++ List(FreeValueContainer.s41, FreeValueContainer.x41, FreeValueContainer.s42, FreeValueContainer.x42, FreeValueContainer.s43, FreeValueContainer.x43, FreeValueContainer.s44, FreeValueContainer.x44, FreeValueContainer.s45, FreeValueContainer.x45) ++ List(FreeValueContainer.s46, FreeValueContainer.x46, FreeValueContainer.s47, FreeValueContainer.x47, FreeValueContainer.s48, FreeValueContainer.x48, FreeValueContainer.s49, FreeValueContainer.x49, FreeValueContainer.s50, FreeValueContainer.x50) ++ List(FreeValueContainer.s51, FreeValueContainer.x51, FreeValueContainer.s52, FreeValueContainer.x52, FreeValueContainer.s53, FreeValueContainer.x53, FreeValueContainer.s54, FreeValueContainer.x54, FreeValueContainer.s55, FreeValueContainer.x55) ++ List(FreeValueContainer.s56, FreeValueContainer.x56, FreeValueContainer.s57, FreeValueContainer.x57, FreeValueContainer.s58, FreeValueContainer.x58, FreeValueContainer.s59, FreeValueContainer.x59, FreeValueContainer.s60, FreeValueContainer.x60) ++ List(FreeValueContainer.s61, FreeValueContainer.x61, FreeValueContainer.s62, FreeValueContainer.x62, FreeValueContainer.s63, FreeValueContainer.x63, FreeValueContainer.s64, FreeValueContainer.x64, FreeValueContainer.s65, FreeValueContainer.x65) ++ List(FreeValueContainer.s66, FreeValueContainer.x66, FreeValueContainer.s67, FreeValueContainer.x67, FreeValueContainer.s68, FreeValueContainer.x68, FreeValueContainer.s69, FreeValueContainer.x69, FreeValueContainer.s70, FreeValueContainer.x70) ++ List(FreeValueContainer.s71, FreeValueContainer.x71, FreeValueContainer.s72, FreeValueContainer.x72, FreeValueContainer.s73, FreeValueContainer.x73, FreeValueContainer.s74, FreeValueContainer.x74, FreeValueContainer.s75, FreeValueContainer.x75) ++ List(FreeValueContainer.s76, FreeValueContainer.x76, FreeValueContainer.s77, FreeValueContainer.x77, FreeValueContainer.s78, FreeValueContainer.x78, FreeValueContainer.s79, FreeValueContainer.x79, FreeValueContainer.s80, FreeValueContainer.x80) ++ List(FreeValueContainer.s81, FreeValueContainer.x81, FreeValueContainer.s82, FreeValueContainer.x82, FreeValueContainer.s83, FreeValueContainer.x83, FreeValueContainer.s84, FreeValueContainer.x84, FreeValueContainer.s85, FreeValueContainer.x85) ++ List(FreeValueContainer.s86, FreeValueContainer.x86, FreeValueContainer.s87, FreeValueContainer.x87, FreeValueContainer.s88, FreeValueContainer.x88, FreeValueContainer.s89, FreeValueContainer.x89, FreeValueContainer.s90, FreeValueContainer.x90) ++ List(FreeValueContainer.s91, FreeValueContainer.x91, FreeValueContainer.s92, FreeValueContainer.x92, FreeValueContainer.s93, FreeValueContainer.x93, FreeValueContainer.s94, FreeValueContainer.x94, FreeValueContainer.s95, FreeValueContainer.x95) ++ List(FreeValueContainer.s96, FreeValueContainer.x96, FreeValueContainer.s97, FreeValueContainer.x97, FreeValueContainer.s98, FreeValueContainer.x98, FreeValueContainer.s99, FreeValueContainer.x99, FreeValueContainer.s100, FreeValueContainer.x100))
          }
        }         
     
      
      }
    

    }

  