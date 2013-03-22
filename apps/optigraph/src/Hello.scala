package ppl.apps.graph.TestApp

import ppl.dsl.optigraph._
import ppl.delite.framework.DeliteApplication

object Outside {
  val x = 0
  val m = "hello"
}

object Hello extends OptiGraphApplicationRunner with OptiGraphHello

/*
 *  -----------------------------------------
 *  OptiGraph tests / sample applications
 *  -----------------------------------------
*/

trait OptiGraphHello extends OptiGraphApplication {
  def main() {
    println(Outside.m)
    val g  = Graph()
    val n1 = g.AddNode
    val n2 = g.AddNode
    val x = 3
    val y = Outside.x + 1
    println(x + y)
    val e1 = g.AddEdge(n1, n2)
  }
}