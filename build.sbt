import org.openurp.parent.Dependencies._
import org.openurp.parent.Settings._

ThisBuild / organization := "org.openurp.std.exchange"
ThisBuild / version := "0.0.2-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/std-exchange"),
    "scm:git@github.com:openurp/std-exchange.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "chaostone",
    name  = "Tihua Duan",
    email = "duantihua@gmail.com",
    url   = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Std Exchange"
ThisBuild / homepage := Some(url("http://openurp.github.io/std-exchange/index.html"))

val apiVer = "0.31.0.Beta2"
val starterVer = "0.2.10"
val baseVer = "0.3.3"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer

lazy val root = (project in file("."))
  .settings(
    name := "openurp-std-exchange-parent"
  ).aggregate(core,web,webapp)

lazy val core = (project in file("core"))
  .settings(
    name := "openurp-std-exchange-core",
    common,
    libraryDependencies ++= Seq(openurp_edu_api,openurp_std_api,beangle_ems_app)
  )

lazy val web = (project in file("web"))
  .settings(
    name := "openurp-std-exchange-web",
    common,
    libraryDependencies ++= Seq(openurp_stater_web,openurp_base_tag)
  ).dependsOn(core)

lazy val webapp = (project in file("webapp"))
  .enablePlugins(WarPlugin,TomcatPlugin)
  .settings(
    name := "openurp-std-exchange-webapp",
    common
  ).dependsOn(web)

publish / skip := true