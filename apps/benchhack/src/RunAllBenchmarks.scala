object RunAllBenchmarks {

  def main(args: Array[String]): Unit = {
    optigraphOptimizedScalameter.main(Array[String]())
    optigraphUnoptimizedScalameter.main(Array[String]())
    RealOptigraphScalameter.main(Array[String]())
  }

}
