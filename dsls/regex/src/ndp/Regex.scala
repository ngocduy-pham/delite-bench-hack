package ndp

import scala.virtualization.lms.common._
import scala.virtualization.lms.internal._
import scala.tools.nsc.io._
import ppl.delite.framework.{ Config, DeliteApplication }

import ppl.delite.framework.codegen.Target
import ppl.delite.framework.codegen.Utils
import ppl.delite.framework.codegen.scala.TargetScala
import ppl.delite.framework.codegen.cuda.TargetCuda
import ppl.delite.framework.codegen.c.TargetC
import ppl.delite.framework.ops._
import ppl.delite.framework.datastructures._
import ppl.delite.framework.codegen.delite.overrides._

trait RegexApplicationRunner extends RegexApplication with DeliteApplication with RegexExp {
  override protected def collectInDef(df: Def[Any]): scala.List[Any] =
    df match {
      case Matches(s, p) => collectInExp(s) ++ collectInExp(p)
      case _             => super.collectInDef(df)
    }
}

trait RegexApplication extends Regex with RegexLift {
  var args: Rep[Array[String]]
  def main(): Unit
}

trait RegexLift extends LiftScala { // allow apps to use all of Scala
  this: Regex =>
}

trait Regex extends ScalaOpsPkg with RegexOps

trait RegexExp extends Regex with ScalaOpsPkgExp with RegexOpsExp with DeliteOpsExp with DeliteAllOverridesExp {

  this: DeliteApplication with RegexApplication with RegexExp =>

  def getCodeGenPkg(t: Target { val IR: RegexExp.this.type }): GenericFatCodegen { val IR: RegexExp.this.type } = {

    t match {
      case _: TargetScala => new RegexCodeGenScala {
        val IR: RegexExp.this.type = RegexExp.this
      }
      case _ => throw new IllegalArgumentException("unsupported target")
    }
  }
}

trait RegexCodeGenBase extends GenericFatCodegen with Utils {
  val IR: DeliteApplication with RegexExp
  override def initialDefs = IR.deliteGenerator.availableDefs

  def dsmap(s: String) = {
    var res = s.replaceAll("ndp.datastruct", "generated")
    res.replaceAll("ndp", "generated.scala")
  }

  override def remap[A](m: Manifest[A]): String = dsmap(super.remap(m))

  override def emitDataStructures(path: String) {
    val s = File.separator
    val dsRoot = Config.homeDir + s + "dsls" + s + "profiling" + s + "src" + s + "example" + s + "profiling" + s + "datastruct" + s + this.toString

    copyDataStructures(dsRoot, path, dsmap)
  }
}

trait RegexCodeGenScala extends RegexCodeGenBase with ScalaCodeGenPkg with ScalaGenDeliteOps with ScalaGenRegexOps {

  val IR: DeliteApplication with RegexExp
}
