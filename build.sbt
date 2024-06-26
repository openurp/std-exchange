import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*

ThisBuild / organization := "org.openurp.std.exchange"
ThisBuild / version := "0.0.2"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/openurp/std-exchange"),
    "scm:git@github.com:openurp/std-exchange.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "OpenURP Std Exchange"
ThisBuild / homepage := Some(url("http://openurp.github.io/std-exchange/index.html"))

val apiVer = "0.39.1"
val starterVer = "0.3.32"
val baseVer = "0.4.24"
val eduCoreVer = "0.2.6"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_web = "org.openurp.starter" % "openurp-starter-web" % starterVer
val openurp_base_tag = "org.openurp.base" % "openurp-base-tag" % baseVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer

lazy val root = (project in file("."))
  .settings(
    name := "openurp-std-exchange-parent"
  ).aggregate(core, webapp)

lazy val core = (project in file("core"))
  .settings(
    name := "openurp-std-exchange-core",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_std_api, beangle_ems_app, openurp_edu_core)
  )

lazy val webapp = (project in file("webapp"))
  .enablePlugins(WarPlugin, TomcatPlugin)
  .settings(
    name := "openurp-std-exchange-webapp",
    common,
    libraryDependencies ++= Seq(openurp_stater_web, openurp_base_tag)
  ).dependsOn(core)

publish / skip := true