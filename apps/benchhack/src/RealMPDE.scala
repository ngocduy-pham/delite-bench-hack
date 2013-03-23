import ppl.dsl.optigraph._

object RealMPDERunner extends OptiGraphApplicationRunner with RealMPDE

trait RealMPDE extends OptiGraphApplication {

  def main() {
    val g  = Graph()
    val n1 = g.AddNode
    val n2 = g.AddNode
    val e1 = g.AddEdge(n1, n2)
    // repeated edge
    val e2 = g.AddEdge(n1, n2)
    val e3 = g.AddEdge(n2, n1)
    // self-edge
    val e4 = g.AddEdge(n1, n1)
    g.Freeze
    
    println("Test Graph")
    if(g.NumNodes != FreeValueContainer.x2) {
      println("[FAIL] Wrong number of nodes. Expected value = FreeValueContainer.x2, Actual value " + g.NumNodes)
    } else {
      println("[OK] Number of nodes is correct")
    }
    
    if(g.NumEdges != FreeValueContainer.x14) {
      println("[FAIL] Wrong number of edges. Expected value = FreeValueContainer.x14, Actual value " + g.NumNodes)
    } else {
      println("[OK] Number of edges is correct")
    }
    
    if(g.Node(n1.Id).Id != n1.Id) {
      println("[FAIL] Wrong node returned")
    } else {
      println("[OK] Correct node returned")
    }
    if(g.Edge(e1.Id).Id != e1.Id) {
      println("[FAIL] Wrong edge returned")
    } else {
      println("[OK] Correct edge returned")
    }
    
    val nodes = g.Nodes.toSet
    val edges = g.Edges.toSet
    if(nodes.Size != FreeValueContainer.x2 || edges.Size != FreeValueContainer.x14) {
      println("[FAIL] Wrong nodes/edges collection size")
    } else {
      println("[OK] Correct nodes/edges collection size")
    }
    if((!nodes.Has(n1)) || (!nodes.Has(n2)) || (!edges.Has(e1)) || (!edges.Has(e2))
        || (!edges.Has(e3)) || (!edges.Has(e4))) {
      println("[FAIL] Wrong nodes/edges collection contents")
    } else {
      println("[OK] Correct nodes/edges collection contents")
    }
    
    //---------//
    
    val g2  = DGraph()
    val n3 = g2.AddNode
    val n4 = g2.AddNode
    val e5 = g2.AddEdge(n3, n4)
    val e6 = g2.AddEdge(n3, n4)
    val e7 = g2.AddEdge(n4, n3)
    val e8 = g2.AddEdge(n3, n3)
    val g_s = g2.Snapshot
    
    if(g_s.NumNodes != g2.NumNodes || g_s.NumEdges != g2.NumEdges) {
      println("[FAIL] Wrong snapshot graph size")
    } else {
      println("[OK] Correct snapshot graph size")
    }
    
    if(!((g_s.Node(FreeValueContainer.x).Degree == FreeValueContainer.x30 || g_s.Node(1).Degree == FreeValueContainer.x30) && 
        (g_s.Node(FreeValueContainer.x).Degree == FreeValueContainer.x1 || g_s.Node(FreeValueContainer.x1).Degree == FreeValueContainer.x1))) {
      println("[FAIL] Wrong snapshot graph node connections")
    } else {
      println("[OK] Correct snapshot graph node connections")
    }
    
    val g3 = UGraph()
    val n5 = g3.AddNode
    val n6 = g3.AddNode
    val e9 = g3.AddEdge(n5, n6)
    g3.Freeze
    if((n5.OutDegree != FreeValueContainer.x1) && (n6.OutDegree != FreeValueContainer.x1) && (n5.InDegree != FreeValueContainer.x1) && (n6.InDegree != FreeValueContainer.x1)) {
      println("[FAIL] Undirected graph wrong edge connectivity")
    } else {
      println("[OK] Undirected graph connectivity is correct")
    }
    
  }
  
  def test_deferrable() {
    println("Test Deferrable")
    val d = Deferrable[Double](FreeValueContainer.x10 * 1.0)
    if(d.value != FreeValueContainer.x10 * 1.0) {
      println("[FAIL] Expected value = FreeValueContainer.x10, Actual value = " + d.value)
    } else {
      println("[OK] Current value is correct")
    }
    d <= FreeValueContainer.x7 * 1.0
    if(d.value != FreeValueContainer.x10 * 1.0) {
      println("[FAIL] After deferral, expected value = FreeValueContainer.x10, Actual value = " + d.value)
    } else {
      println("[OK] Deferral did not affect current value")
    }
    d.assign()
    if(d.value != FreeValueContainer.x7 * 1.0) {
      println("[FAIL] After assignment, expected value = FreeValueContainer.x7, Actual value = " + d.value)
    } else {
      println("[OK] Current value is correct after assignment")
    }
   
    d.setValue(FreeValueContainer.x10 * 1.0)
    d.assign()
    if(d.value != FreeValueContainer.x10 * 1.0) {
      println("[FAIL] After repeated assignment, expected value = FreeValueContainer.x10, Actual value = " + d.value)
    } else {
      println("[OK] Repeated assignment did not affect value")
    }

  }

}