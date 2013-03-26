package ndp

import scala.reflect.SourceContext
import scala.virtualization.lms.common.Base
import scala.virtualization.lms.common.EffectExp
import scala.virtualization.lms.common.ScalaGenStringOps
import scala.virtualization.lms.common.StringOpsExp

trait RegexOps extends Base {
  def matches(s: Rep[String], pattern: Rep[String])(implicit pos: SourceContext): Rep[Boolean]
}

trait RegexOpsExp extends StringOpsExp with EffectExp {

  case class Matches(s: Exp[String], pattern: Exp[String]) extends Def[Boolean]

  def matches(s: Exp[String], pattern: Exp[String])(implicit pos: SourceContext): Exp[Boolean] =
    reflectEffect(Matches(s, pattern))
}

trait ScalaGenRegexOps extends ScalaGenStringOps {
  val IR: RegexOpsExp
  import IR._

  override def emitNode(sym: Sym[Any], rhs: Def[Any]) = rhs match {
    case Matches(s, pattern) => emitValDef(sym, s"(new scala.util.matching.Regex(${quote(pattern)}) findFirstIn ${quote(s)}).isEmpty")
    case _                   => super.emitNode(sym, rhs)
  }

  /*override def emitFileHeader(): Unit = {
    stream.println("import scala.util.matching.Regex")
  }*/

}
