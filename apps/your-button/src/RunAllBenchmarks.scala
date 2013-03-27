object RunAllBenchmarks {

  def main(args: Array[String]): Unit = {
    val toRun = List(
      scalameter_deliteLOC_optimized,
      scalameter_deliteLOC_unoptimized,
      scalameter_yinyangLOC,
      scalameter_5nodes,
      scalameter_realLife_delite_optimized,
      //scalameter_realLife_delite_unoptimized,
      scalameter_realLife_yinyang,
      scalameter_regex_delite_guard,
      scalameter_regex_delite_computation
    )
    for (scalameter <- toRun) scalameter main Array[String]()
    for (scalameter <- toRun) println(scalameter.outputFormat)
  }

}
