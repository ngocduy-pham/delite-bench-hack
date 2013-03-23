import ppl.dsl.optigraph._

object RealOptiGraphRunner extends OptiGraphApplicationRunner with RealOptiGraph

trait RealOptiGraph extends OptiGraphApplication {

  def main() {
    val g = Graph()
    val n1 = g.AddNode
    val n2 = g.AddNode
    val n3 = g.AddNode
    val n4 = g.AddNode
    g.Freeze
    val np1 = NodeProperty[Int](g, FreeValueContainer.x)
    val np2 = NodeProperty[Boolean](g, true)
    val ns = NodeSet()

    // check empty/non-empty collections, with/without filters

    println("Test Reduction Expressions: SUM")
    val r1 = Sum(g.Nodes) { np1(_) }
    val r2 = Sum(g.Nodes, (n: Rep[Node]) => (n.Id == FreeValueContainer.x)) { np1(_) }
    val r3 = Sum(ns.Items) { np1(_) }

    if (r1 != 4) {
      println("[FAIL] Expected value = 4, Actual value = " + r1)
    }
    else {
      println("[OK] Sum is correct.")
    }
    if (r2 != FreeValueContainer.x) {
      println("[FAIL] Expected value = 1, Actual value = " + r2)
    }
    else {
      println("[OK] Sum is correct.")
    }
    if (r3 != 0) {
      println("[FAIL] Expected value = 0, Actual value = " + r3)
    }
    else {
      println("[OK] Sum is correct.")
    }

    //-------//

    println("Test Reduction Expressions: PRODUCT")
    r1 = Product(g.Nodes) { np1(_) }
    r2 = Product(g.Nodes, (n: Rep[Node]) => (n.Id == FreeValueContainer.x)) { n => unit(5) }
    r3 = Product(ns.Items) { np1(_) }

    if (r1 != FreeValueContainer.x) {
      println("[FAIL] Expected value = 1, Actual value = " + r1)
    }
    else {
      println("[OK] Product is correct.")
    }
    if (r2 != 5) {
      println("[FAIL] Expected value = 5, Actual value = " + r2)
    }
    else {
      println("[OK] Product is correct.")
    }
    if (r3 != 0) {
      println("[FAIL] Expected value = 0, Actual value = " + r3)
    }
    else {
      println("[OK] Product is correct.")
    }

    //-------//

    println("Test Reduction Expressions: COUNT")
    r1 = Count(g.Nodes) { (n: Rep[Node]) => (n.Id != FreeValueContainer.x) }
    r2 = Count(g.Nodes) { (n: Rep[Node]) => (n.Id == FreeValueContainer.x) }
    r3 = Count(ns.Items) { (n: Rep[Node]) => (n.Id == FreeValueContainer.x) }

    if (r1 != 3) {
      println("[FAIL] Expected value = 3, Actual value = " + r1)
    }
    else {
      println("[OK] Count is correct.")
    }
    if (r2 != FreeValueContainer.x) {
      println("[FAIL] Expected value = 1, Actual value = " + r2)
    }
    else {
      println("[OK] Count is correct.")
    }
    if (r3 != 0) {
      println("[FAIL] Expected value = 0, Actual value = " + r3)
    }
    else {
      println("[OK] Count is correct.")
    }

    //-------//

    println("Test Reduction Expressions: MIN")
    r1 = Min(g.Nodes) { (n: Rep[Node]) => n.Id }
    r2 = Min(g.Nodes, (n: Rep[Node]) => (n.Id == FreeValueContainer.x)) { (n: Rep[Node]) => n.Id }
    r3 = Min(ns.Items) { np1(_) }

    if (r1 != 0) {
      println("[FAIL] Expected value = 0, Actual value = " + r1)
    }
    else {
      println("[OK] Min is correct.")
    }
    if (r2 != FreeValueContainer.x) {
      println("[FAIL] Expected value = 1, Actual value = " + r2)
    }
    else {
      println("[OK] Min is correct.")
    }
    if (r3 != MAX_INT) {
      println("[FAIL] Expected value = MAX_INT, Actual value = " + r3)
    }
    else {
      println("[OK] Min is correct.")
    }

    //-------//

    println("Test Reduction Expressions: MAX")
    r1 = Max(g.Nodes) { (n: Rep[Node]) => n.Id }
    r2 = Max(g.Nodes, (n: Rep[Node]) => (n.Id == FreeValueContainer.x)) { (n: Rep[Node]) => n.Id }
    r3 = Max(ns.Items) { np1(_) }

    if (r1 != 3) {
      println("[FAIL] Expected value = 3, Actual value = " + r1)
    }
    else {
      println("[OK] Max is correct.")
    }
    if (r2 != FreeValueContainer.x) {
      println("[FAIL] Expected value = 1, Actual value = " + r2)
    }
    else {
      println("[OK] Max is correct.")
    }
    if (r3 != MIN_INT) {
      println("[FAIL] Expected value = MIN_INT, Actual value = " + r3)
    }
    else {
      println("[OK] Max is correct.")
    }

    //-------//

    println("Test Reduction Expressions: ALL")
    val r1b = All(g.Nodes) { (n: Rep[Node]) => (n.Id < 5) }
    val r2b = All(g.Nodes, (n: Rep[Node]) => (n.Id == FreeValueContainer.x)) { (n: Rep[Node]) => (n.Id == 2) }
    val r3b = All(ns.Items) { (n: Rep[Node]) => (n.Id < 5) }

    if (r1b != true) {
      println("[FAIL] Expected value = true, Actual value = " + r1b)
    }
    else {
      println("[OK] All is correct.")
    }
    if (r2b != false) {
      println("[FAIL] Expected value = false, Actual value = " + r2b)
    }
    else {
      println("[OK] All is correct.")
    }
    if (r3b != true) {
      println("[FAIL] Expected value = true, Actual value = " + r3b)
    }
    else {
      println("[OK] All is correct.")
    }

    //-------//

    println("Test Reduction Expressions: ANY")
    r1b = Any(g.Nodes) { (n: Rep[Node]) => (n.Id > 2) }
    r2b = Any(g.Nodes, (n: Rep[Node]) => (n.Id == FreeValueContainer.x)) { (n: Rep[Node]) => (n.Id == 2) }
    r3b = Any(ns.Items) { (n: Rep[Node]) => (n.Id < 5) }

    if (r1b != true) {
      println("[FAIL] Expected value = true, Actual value = " + r1b)
    }
    else {
      println("[OK] Any is correct.")
    }
    if (r2b != false) {
      println("[FAIL] Expected value = false, Actual value = " + r2b)
    }
    else {
      println("[OK] Any is correct.")
    }
    if (r3b != false) {
      println("[FAIL] Expected value = false, Actual value = " + r3b)
    }
    else {
      println("[OK] Any is correct.")
    }

  }

}