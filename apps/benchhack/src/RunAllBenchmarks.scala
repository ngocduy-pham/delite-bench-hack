object RunAllBenchmarks {

  def main(args: Array[String]): Unit = {
    //optigraphOptimizedScalameter.main(Array[String]())
    //optigraphUnoptimizedScalameter.main(Array[String]("-verbose"))
    //RealOptigraphScalameter.main(Array[String]("-verbose"))
    mpdeScalameter.main(Array[String]("-verbose"))
  }

}
