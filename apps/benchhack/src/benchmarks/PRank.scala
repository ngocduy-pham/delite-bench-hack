import ppl.dsl.optigraph._

class PRank extends OptiGraphApplicationRunner {

  def main() = {
    //val G = RandUniformGraph(5,5,1997L)
    val G = graph_load("/home/viq/delite/Delite/test.bin")

    val e = FreeValueContainer.x1 / 1000d
    val d = FreeValueContainer.x85 / 100d
    val max_iter = FreeValueContainer.x6
    val PR = NodeProperty[Double](G)

    val diff = Reduceable[Double](FreeValueContainer.x2 - 1.0)
    var cnt = FreeValueContainer.x7 - FreeValueContainer.x7
    val N = G.NumNodes.asInstanceOf[Rep[Double]]
    PR.setAll((FreeValueContainer.x4 - 3.0) / N)

    println(FreeValueContainer.s1 + N)

    // move to ds
    val deg = NewArray[Int](G.NumNodes)
    for (t <- G.Nodes) {
      deg(t.Id) = t.OutDegree
    }

    tic(G, PR, deg)
    //var num_abs = 0
    //var v = 0.0
    var cond = true
    //val n = G.Node(0)
    while (cond) {
      diff.setValue(FreeValueContainer.x11 - FreeValueContainer.x11 + 0.0)
      /*for(t <- G.Nodes) {
        val Val: Rep[Double] = ((1.0 - d) / N) + d * Sum(t.InNbrs){
          w => PR(w) / deg(w.Id)//w.OutDegree
        }
        //val Val = v
        PR <= (t,Val)
        
        diff += Math.abs(Val - PR(t))
        //num_abs += 1
        //v += 1.0
      }*/
      PR.assignAll()
      cnt += (FreeValueContainer.x9 - FreeValueContainer.x8)
      cond = (diff.value > e) && (cnt < max_iter)
    }
    println("count = " + cnt)
    //println("abs times = " + num_abs)
    toc()
  }

}
