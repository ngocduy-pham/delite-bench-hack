object MPDESpitter extends Spitter {

  def valobjName(id: Int): String = s"mpdeValobj$id"

  def name(id: Int): String = s"mpdeBench$id"

  def startFile(id: Int, valobjName: String): String = s"""
    import ppl.dsl.optiml._
    ${valobj(valobjName)}
    object ${name(id)} extends OptiGraphApplicationRunner {
      def main() = {
  """

  def interval(id: Int, valobj: String): String = s"""
        val in$id = Matrix.onesf($id, 2 * $id)
        val out$id = in$id.sigmoidf
        val x$id = 10 * $id
        for (i <- 1 to 4) println(x)
        out$id.pprint
  """

}
