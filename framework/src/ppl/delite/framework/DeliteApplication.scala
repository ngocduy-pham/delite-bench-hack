package ppl.delite.framework

import java.io.{ FileWriter, File, PrintWriter }
import scala.collection.mutable.{ Map => MMap }
import scala.tools.nsc.io._
import scala.virtualization.lms.common.{ BaseExp, Base }
import scala.virtualization.lms.internal.{ GenericFatCodegen, ScalaCompile, GenericCodegen, ScalaCodegen, Transforming }

import analysis.{ MockStream, TraversalAnalysis }
import codegen.c.TargetC
import codegen.cuda.TargetCuda
import codegen.delite.{ DeliteCodeGenPkg, DeliteCodegen, TargetDelite }
import codegen.opencl.TargetOpenCL
import codegen.scala.TargetScala
import codegen.Target
import ops.DeliteOpsExp
import transform.DeliteTransform
import scala.virtualization.lms.internal._

trait DeliteApplication extends DeliteOpsExp with ScalaCompile with DeliteTransform with Expressions {
  type DeliteApplicationTarget = Target { val IR: DeliteApplication.this.type }

  private val optimized = false

  /*
   * code generators
   */
  def getCodeGenPkg(t: DeliteApplicationTarget): GenericFatCodegen { val IR: DeliteApplication.this.type }

  lazy val scalaTarget = new TargetScala { val IR: DeliteApplication.this.type = DeliteApplication.this }
  lazy val cudaTarget = new TargetCuda { val IR: DeliteApplication.this.type = DeliteApplication.this }
  lazy val cTarget = new TargetC { val IR: DeliteApplication.this.type = DeliteApplication.this }
  lazy val openclTarget = new TargetOpenCL { val IR: DeliteApplication.this.type = DeliteApplication.this }

  var targets = List[DeliteApplicationTarget](scalaTarget)
  if (Config.generateCUDA)
    targets = cudaTarget :: targets
  if (Config.generateC)
    targets = cTarget :: targets
  if (Config.generateOpenCL)
    targets = openclTarget :: targets

  val generators: List[GenericFatCodegen { val IR: DeliteApplication.this.type }] = targets.reverse.map(getCodeGenPkg(_))

  // TODO: refactor, this is from ScalaCompile trait
  lazy val codegen: ScalaCodegen { val IR: DeliteApplication.this.type } =
    getCodeGenPkg(scalaTarget).asInstanceOf[ScalaCodegen { val IR: DeliteApplication.this.type }]

  // generators created by getCodeGenPkg will use the 'current' scope of the deliteGenerator as global scope
  val deliteGenerator = new DeliteCodeGenPkg {
    val IR: DeliteApplication.this.type = DeliteApplication.this
    val generators = DeliteApplication.this.generators;
  }

  /*
   * analyses
   */
  lazy val analyses: List[TraversalAnalysis { val IR: DeliteApplication.this.type }] = List()

  // Store and retrieve
  val analysisResults = MMap[String, Any]()

  /*
   * misc state
   */
  var args: Rep[Array[String]] = _

  var staticDataMap: Map[String, _] = _

  protected def collectInBlock(block: Block[Any]): List[Any] =
    deliteGenerator.focusBlock(block) {
      deliteGenerator.focusExactScope(block) { levelScope =>
        levelScope flatMap (stm => {
          stm match {
            case TP(_, rhs)       => collectInDef(rhs)
            case TTP(_, mhs, rhs) => (mhs flatMap collectInDef) ++ collectInFatDef(rhs)
            case _                => scala.Predef.println("Unmatched statement: " + stm); Nil
          }
        })
      }
    }

  protected def collectInDef(df: Def[Any]): scala.List[Any] =
    df match {
      case Reflect(x, _, deps)       => collectInDef(x) ++ (deps flatMap collectInExp)
      case Reify(x, _, effects)      => collectInExp(x) ++ (effects flatMap collectInExp)
      case Forward(x)                => collectInExp(x)

      case StaticData(_)             => Nil
      case ReadVar(_)                => Nil
      case NewVar(init)              => collectInExp(init)
      case Assign(_, rhs)            => collectInExp(rhs)
      case VarPlusEquals(_, rhs)     => collectInExp(rhs)
      case VarMinusEquals(_, rhs)    => collectInExp(rhs)
      case VarTimesEquals(_, rhs)    => collectInExp(rhs)
      case VarDivideEquals(_, rhs)   => collectInExp(rhs)

      case ArrayNew(n)               => collectInExp(n)
      case ArrayFromSeq(_)           => Nil
      case ArrayApply(a, n)          => collectInExp(a) ++ collectInExp(n)
      case ArrayUpdate(a, n, y)      => collectInExp(a) ++ collectInExp(n) ++ collectInExp(y)
      case ArrayLength(a)            => collectInExp(a)
      case ArrayForeach(a, _, block) => collectInExp(a) ++ collectInBlock(block)
      case ArrayCopy(src, srcPos, dest, destPos, len) =>
        collectInExp(src) ++ collectInExp(srcPos) ++ collectInExp(dest) ++ collectInExp(destPos) ++ collectInExp(len)

      /* case ArraySort(x: Exp[Array[T]])
          case ArrayMap[A: Manifest, B: Manifest](a: Exp[Array[A]], x: Sym[A], block: Block[B])
          case ArrayToSeq[A: Manifest](x: Exp[Array[A]])*/

      case _ => scala.Predef.println("Unmatched def: " + df); Nil
    }

  protected def collectInExp(exp: Exp[Any]): scala.List[Any] =
    exp match {
      case Combine(ls) => ls flatMap collectInExp
      case Const(c)    => List(c)
      case Sym(_)      => Nil
      case _           => Nil
    }

  var previous: scala.collection.mutable.ArrayBuffer[Any] = _
  protected def collectInFatDef(fd: FatDef): scala.List[Const[Any]] = Nil

  // private var constBuff: List[Any] = null

  final def main(args: Array[String]) {
    super.reset
    //val startPoint = System.currentTimeMillis()
    //println("Delite Application Being Staged:[" + this.getClass.getName + "]")

    //println("******Generating the program******")

    // set transformers to be applied before codegen
    deliteGenerator.transformers = transformers

    //System.out.println("Staging application")

    val optimized = args.head == "true"
    if (optimized) {
      val s = fresh[Array[String]]
      val block = deliteGenerator.reifyBlock(liftedMain(s))
      // val block = deliteGenerator.runTransformations(body) we do not need this
      /*deliteGenerator.focusBlock(block) {
        deliteGenerator.focusExactScope(block) { levelScope =>
          levelScope foreach (stm => {
            println(stm)
          })
        }
      }*/
      // val current = collectInBlock(block) /* foreach println*/
      val current = constBuff
      if (previous == null) {
        println("init")
        previous = new scala.collection.mutable.ArrayBuffer[Any]() ++ current
      }
      else if (current != constBuff) {
        println("recompile")
        previous.clear()
        previous ++= current
      }
      else {
        println("retrieve")
      }

    }
    else {
      val stream = new PrintWriter(new FileWriter(Config.degFilename))

      /*val stream =
      if (Config.degFilename == "") {
        new PrintWriter(System.out)
      }
      else {
        new PrintWriter(new FileWriter(Config.degFilename))
      }*/

      def writeModules(baseDir: String) {
        Directory(Path(baseDir)).createDirectory()
        val writer = new FileWriter(baseDir + "modules.dm")
        writer.write("datastructures:\n")
        writer.write("kernels:datastructures\n")
        writer.close()
      }

      // System.out.println("Running analysis")

      // Run any analyses defined
      /*for(a <- analyses) {
      val baseDir = Config.buildDir + File.separator + a.toString + File.separator
      a.initializeGenerator(baseDir + "kernels" + File.separator, args, analysisResults)
      a.traverse(liftedMain) match {
        case Some(result) => { analysisResults(a.className) = result }
        case _ =>
      }
    }*/

      deliteGenerator.emitDataStructures(Config.buildDir + File.separator)

      for (g <- generators) {
        //TODO: Remove c generator specialization
        val baseDir = Config.buildDir + File.separator + (if (g.toString == "c") "cuda" else g.toString) + File.separator // HACK! TODO: HJ, fix
        writeModules(baseDir)
        g.emitDataStructures(baseDir + "datastructures" + File.separator)
        g.initializeGenerator(baseDir + "kernels" + File.separator, args.tail, analysisResults)
      }

      /*if (Config.debug) {
      if (Config.degFilename.endsWith(".deg")) {
        val streamScala = new PrintWriter(new FileWriter(Config.degFilename.replace(".deg",".scala")))
        val baseDir = Config.buildDir + File.separator + codegen.toString + File.separator
        codegen.initializeGenerator(baseDir + "kernels" + File.separator, args, analysisResults) // whole scala application (for testing)
        codegen.emitSource(liftedMain, "Application", streamScala) // whole scala application (for testing)
        // TODO: dot output
        reset
      }
    }*/

      deliteGenerator.initializeGenerator(Config.buildDir, args.tail, analysisResults)
      val sd = deliteGenerator.emitSource(liftedMain, "Application", stream)
      deliteGenerator.finalizeGenerator()

      /*if (Config.printGlobals) {
        println("Global definitions")
        for (globalDef <- globalDefs) {
          println(globalDef)
        }
      }*/

      // *** they just keep re-generating code

      generators foreach { _.finalizeGenerator() }

      staticDataMap = Map() ++ sd map { case (s, d) => (deliteGenerator.quote(s), d) }

      val degFile = new File(Config.degFilename)

      /*print("static data: ")
      staticDataMap foreach println
      println("")*/

      // System.out.println("Compile and execute generated code")

      import ppl.delite.runtime.{ Delite, Config }

      //load task graph
      val graph = Delite loadDeliteDEG degFile.getCanonicalPath()
      //val graph = new TestGraph
      Config.deliteBuildHome = graph.kernelPath
      //load kernels & data structures
      Delite loadSources graph
      //Delite main scala.Array(degFile.getCanonicalPath())
    }
    //System.out.println(s"All run in ${(System.currentTimeMillis() - startPoint) / 1000d}s")
  }

  final def generateScalaSource(name: String, stream: PrintWriter) = {
    reset
    stream.println("object " + name + "Main {" /*}*/ )
    stream.println("def main(args: Array[String]) {" /*}*/ )
    stream.println("val o = new " + name)
    stream.println("o.apply(args)")
    stream.println("ppl.delite.runtime.profiler.PerformanceTimer.print(\"app\")")
    stream.println( /*{*/ "}")
    stream.println( /*{*/ "}")
    codegen.emitSource(liftedMain, name, stream)
  }

  final def execute(args: Array[String]) {
    println("Delite Application Being Executed:[" + this.getClass.getName + "]")

    globalDefs = List()
    val g = compile(liftedMain)
    g(args)
  }

  /** this is the entry method for our applications, user implement this method. Note, that it is missing the
    * args parameter, args are now accessed via the args field. This basically hides the notion of Reps from
    * user code
    */
  def main(): Unit

  def liftedMain(x: Rep[Array[String]]) = {
    //runUntilJitted  {
    //      reset()
    this.args = x
    val y = main()
    //      collected_consts == previous consts
    //previous_consts = collected_consts
    //    }
    this.args = null
    unit(y)
  }

  private def nop = throw new RuntimeException("not implemented yet")
}
