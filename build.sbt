name := "KvitMaker"

version := "1.0"

lazy val `kvitmaker` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.6"
)

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq("org.apache.poi" % "poi" % "3.14",
  "org.apache.poi" % "poi-ooxml" % "3.14",
  "org.apache.poi" % "poi-ooxml-schemas" % "3.14")

libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies += "org.zeroturnaround" % "zt-zip" % "1.9"