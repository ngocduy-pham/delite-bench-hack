object MPDESpitter extends Spitter {

  val scalameterName: String = "mpdeBench"

  def name(id: Int): String = s"mpdeBench$id"

  def startFile(id: Int): String = s"""
    import ppl.dsl.optiml._
     object ${name(id)} extends OptiGraphApplicationRunner {
      def main() = {
  """

  def interval(id: Int): String = s"""
        val in$id = Matrix.onesf($id, 2 * $id)
        val out$id = in$id.sigmoidf
        val x$id = 10 * $id
        for (i <- 1 to 4) println(x)
        out$id.pprint
  """

  def measuring(toBench: List[String]): String = ???

}
