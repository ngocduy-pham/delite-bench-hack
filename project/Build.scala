import sbt._
import Keys._

object DeliteBuild extends Build {
  val virtualization_lms_core =  "EPFL" % "lms_2.10" % "0.3-SNAPSHOT"

  // -DshowSuppressedErrors=false
  System.setProperty("showSuppressedErrors", "false")

  // FIXME: custom-built scalatest
  val dropboxScalaTestRepo = "Dropbox" at "http://dl.dropbox.com/u/12870350/scala-virtualized"

  //val scalatestCompile = "org.scalatest" % "scalatest_2.10.0-virtualized-SNAPSHOT" % "1.6.1-SNAPSHOT" intransitive()
  val scalatestCompile = "org.scalatest" % "scalatest_2.10" % "2.0.M6-SNAP7" intransitive()
  val scalatest = scalatestCompile % "test" 

  //val virtScala = "2.10.0-M1-virtualized" //"2.10.0-virtualized-SNAPSHOT"
  lazy val scalaOrg = "org.scala-lang.virtualized"
  val virtBuildSettingsBase = Defaults.defaultSettings ++ Seq(

    resolvers += dropboxScalaTestRepo,
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers +=  "OSSH" at "https://oss.sonatype.org/content/groups/public",
    resolvers in ThisBuild += ScalaToolsSnapshots,

    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",

    testFrameworks += new TestFramework("org.scalameter.ScalaMeterFramework"),
    
    organization := "stanford-ppl",

    //scalaHome := Some(file("/Users/tiark/scala-virt-m7/build/pack")),
    scalaVersion := "2.10.1-RC1",
    scalaOrganization := scalaOrg,
    //scalaVersion := virtScala,
    scalaBinaryVersion := "2.10",
    publishArtifact in (Compile, packageDoc) := false,
    // needed for scala.tools, which is apparently not included in sbt's built in version
    libraryDependencies <<= scalaVersion(ver => Seq(
      scalaOrg % "scala-library" % ver,
      scalaOrg % "scala-reflect" % ver,
      scalaOrg % "scala-compiler" % ver, 
      "org.scalatest" % "scalatest_2.10" % "2.0.M6-SNAP7" % "test",
      "junit" % "junit" % "4.8.1" % "test", // we need JUnit explicitly
      virtualization_lms_core,
      "com.github.axel22" %% "scalameter" % "0.3",
      "org.apache.commons" % "commons-math" % "2.2"
    )),
    // used in delitec to access jars
    retrieveManaged := true,
    scalacOptions += "-Yno-generic-signatures",
    //scalacOptions += "-verbose",
    scalacOptions += "-Yvirtualize"
  )

  val virtBuildSettings = virtBuildSettingsBase ++ Seq(
    scalaSource in Compile <<= baseDirectory(_ / "src"),
    scalaSource in Test <<= baseDirectory(_ / "tests"),
    parallelExecution in Test := false
  )


  /*
  val vanillaScala = "2.9.1"
  val vanillaBuildSettings = Defaults.defaultSettings ++ Seq(
    //scalaSource in Compile <<= baseDirectory(_ / "src"),
    //scalaVersion := vanillaScala,
    // needed for scala.tools, which is apparently not included in sbt's built in version
    libraryDependencies += "org.scala-lang" % "scala-library" % vanillaScala,
    libraryDependencies += "org.scala-lang" % "scala-compiler" % vanillaScala
  )
  */

  /*
  lazy val getJars = TaskKey[Unit]("get-jars")
  lazy val getJarsTask = getJars <<= (target, fullClasspath in Runtime) map { (target, cp) =>
    println("Target path is: "+target)
    println("Full classpath is: "+cp.map(_.data).mkString(":"))
  }
  */

  // build targets

  // _ forces sbt to choose it as default
  // useless base directory is to avoid compiling leftover .scala files in the project root directory
  lazy val _delite = Project("delite", file("project/boot"),
    settings = Defaults.defaultSettings) aggregate(framework, dsls, runtime, apps, tests)

  lazy val framework = Project("framework", file("framework"), settings = virtBuildSettings) dependsOn(runtime) // dependency to runtime because of Scopes

  lazy val deliteTest = Project("delite-test", file("framework/delite-test"), settings = virtBuildSettings ++ Seq(
    libraryDependencies += scalatestCompile 
  )) dependsOn(framework, runtime)

  lazy val dsls = Project("dsls", file("dsls"), settings = virtBuildSettings) aggregate(optila, optiml, optiql, optimesh, optigraph, opticvx) 
  lazy val optila = Project("optila", file("dsls/optila"), settings = virtBuildSettings) dependsOn(framework, deliteTest)
  lazy val optiml = Project("optiml", file("dsls/optiml"), settings = virtBuildSettings) dependsOn(optila, deliteTest)
  lazy val optiql = Project("optiql", file("dsls/optiql"), settings = virtBuildSettings) dependsOn(framework, deliteTest)
  lazy val optimesh = Project("optimesh", file("dsls/deliszt"), settings = virtBuildSettings) dependsOn(framework, deliteTest)
  lazy val optigraph = Project("optigraph", file("dsls/optigraph"), settings = virtBuildSettings) dependsOn(framework)
  lazy val opticvx = Project("opticvx", file("dsls/opticvx"), settings = virtBuildSettings) dependsOn(framework, deliteTest)

  lazy val apps = Project("apps", file("apps"), settings = virtBuildSettings) aggregate(optimlApps, optiqlApps, optimeshApps, optigraphApps, opticvxApps, interopApps)
  lazy val optimlApps = Project("optiml-apps", file("apps/optiml"), settings = virtBuildSettings) dependsOn(optiml)
  lazy val optiqlApps = Project("optiql-apps", file("apps/optiql"), settings = virtBuildSettings) dependsOn(optiql)
  lazy val optimeshApps = Project("optimesh-apps", file("apps/deliszt"), settings = virtBuildSettings) dependsOn(optimesh)
  lazy val optigraphApps = Project("optigraph-apps", file("apps/optigraph"), settings = virtBuildSettings) dependsOn(optigraph)
  lazy val opticvxApps = Project("opticvx-apps", file("apps/opticvx"), settings = virtBuildSettings) dependsOn(opticvx)
  lazy val interopApps = Project("interop-apps", file("apps/multi-dsl"), settings = virtBuildSettings) dependsOn(optiml, optiql) // dependsOn(dsls) not working

  lazy val benchhack = Project("bench-hack", file("apps/benchhack"), settings = virtBuildSettings) dependsOn(optigraph, regex)
  lazy val benchrun = Project("your-button", file("apps/your-button"), settings = virtBuildSettings) dependsOn(benchhack)
  lazy val regex = Project("regex", file("dsls/regex"), settings = virtBuildSettings) dependsOn(framework)

  lazy val runtime = Project("runtime", file("runtime"), settings = virtBuildSettings)

  lazy val tests = Project("tests", file("tests"), settings = virtBuildSettingsBase ++ Seq(
    scalaSource in Test <<= baseDirectory(_ / "src"),
    parallelExecution in Test := false
    // don't appear to be able to depend on a different scala version simultaneously, so just using scala-virtualized for everything
  )) dependsOn(framework, runtime, optiml, optimlApps, deliteTest)
  //dependsOn(framework % "test->compile;compile->compile", optiml % "test->compile;compile->compile", optiql % "test", optimlApps % "test->compile;compile->compile", runtime % "test->compile;compile->compile")
}
