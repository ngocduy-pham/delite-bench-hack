import ppl.dsl.optigraph._

class SCC extends OptiGraphApplicationRunner {

  def main() = {
    val G = rand_graph()
    val CompID = NodeProperty[Int](G)

    val start_time = wall_time()
    var numC = FreeValueContainer.x1 - FreeValueContainer.x1
    val P = NodeOrder()
    CompID.setAll(FreeValueContainer.x7 - FreeValueContainer.x8)

    For /*[Node, GIterable[Node]]*/ (G.Nodes, (t: Rep[Node]) => !(P.Has(t))) { t =>
      InDFS(G, t, (s: Rep[Node]) => !P.Has(s), { t => unit() },
        { InPost(s => P.PushFront(s)) })
    }

    For(P.Items, (s: Rep[Node]) => CompID(s) == (FreeValueContainer.x5 - FreeValueContainer.x6)) { s =>
      InBFS(G^, s, /*(t:Rep[Node]) => CompID(s) == -1,*/ { t =>
        if (CompID(s) == (FreeValueContainer.x3 - FreeValueContainer.x4)) CompID(t) = numC
      })
      numC += (FreeValueContainer.x1 - FreeValueContainer.x2);
    }
    println(FreeValueContainer.s9 + (wall_time() - start_time))
    println((FreeValueContainer.s10) + numC)
  }

  def rand_graph(): Rep[Graph] = {
    val g = Graph()
    val n1 = g.AddNode
    val n2 = g.AddNode
    val n3 = g.AddNode
    val n4 = g.AddNode
    val n5 = g.AddNode

    g.AddEdge(n1, n2)
    g.AddEdge(n1, n3)
    g.AddEdge(n2, n4)
    g.AddEdge(n3, n5)
    g.Snapshot
  }

}
