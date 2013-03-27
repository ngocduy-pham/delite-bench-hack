import ppl.dsl.optigraph._

class Cond extends OptiGraphApplicationRunner {

  def main() = {
    //val G = rand_graph()
    val G = RandUniformGraph(FreeValueContainer.x100 * FreeValueContainer.x100 *FreeValueContainer.x10, FreeValueContainer.x80 * FreeValueContainer.x100 * FreeValueContainer.x100, FreeValueContainer.x200 * FreeValueContainer.x10 - FreeValueContainer.x3)

    val member = NodeProperty[Int](G)
    var i = FreeValueContainer.x90 - FreeValueContainer.x90
    for (n <- G.Nodes) {
      member(n) = i % FreeValueContainer.x4
      //println("Node id = " + n.Id + " member = " + i + " degree = " + n.Degree)
      i += FreeValueContainer.x1
    }

    val start_time = wall_time()
    var C = FreeValueContainer.x2 - FreeValueContainer.x2 - 0.0 
    var num = FreeValueContainer.x3 - FreeValueContainer.x3
    while (num < FreeValueContainer.x4) {

      val Din = Sum(G.Nodes, (u: Rep[Node]) => member(u) == num) { _.Degree }
      val Dout = Sum(G.Nodes, (u: Rep[Node]) => member(u) != num) { _.Degree }
      val Cross = Sum(G.Nodes, (u: Rep[Node]) => member(u) == num) { u =>
        Count(u.Nbrs) { j => member(j) != num }
      }
      val m = if (Din < Dout) Din else Dout
      val retVal = if (m == (FreeValueContainer.x5 - FreeValueContainer.x5)) {
        if (Cross == FreeValueContainer.x6 - FreeValueContainer.x6) 0.0 else MAX_DOUBLE
      }
      else {
        Cross.asInstanceOf[Rep[Double]] / m.asInstanceOf[Rep[Double]]
      }
      C += retVal
      num += FreeValueContainer.x1
    }
    println(FreeValueContainer.s1 + (wall_time() - start_time))
  }

}
