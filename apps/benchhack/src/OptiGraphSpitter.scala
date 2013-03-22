object OptiGraphSpitter extends Spitter {

  def valobjName(id: Int): String = s"optigraphValobj$id"

  def name(id: Int): String = s"optigraphBench$id"

  def startFile(id: Int, valobjName: String): String = s"""
    import ppl.dsl.optiml._
    ${valobj(valobjName)}
    object ${name(id)} extends OptiGraphApplicationRunner {
      def main() = {
  """

  def interval(id: Int, valobj: String): String = s"""
    println($valobj.s)
    val g$id = Graph()
    val n${id}01 = g$id.AddNode
    val n${id}02 = g$id.AddNode
    val x$id = 3
    val y$id = $valobj.x + 1
    println(x$id + y$id)
    val e${id}01 = g.AddEdge(n${id}01, n${id}02)
    val n${id}03 = g.AddNode
    g.AddEdge(n${id}02, n${id}03)
  """

}
