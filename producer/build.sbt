name := "producer"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += {
  sys.props += "packaging.type" -> "jar"
  "org.apache.pulsar" % "pulsar-client" % "2.1.0-incubating"
  //  "org.apache.pulsar" % "pulsar-client-2x-shaded" % "2.6.0"
}
