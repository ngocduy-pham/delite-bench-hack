import ppl.dsl.optiml._

object XX {
  val x = 1
}

object HelloWorldRunner extends OptiMLApplicationRunner with OptiMLApplication {
    def main() = {
    val y = XX.x
      println("hello world")
      println(y)
    }
  }
