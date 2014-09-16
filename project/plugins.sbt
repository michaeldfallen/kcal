resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.4")

resolvers += Resolver.url(
  "bintray-sbt-plugin-michaelallen",
  url("https://dl.bintray.com/michaelallen/sbt-plugins/")
)(Resolver.ivyStylePatterns)

resolvers += "bintray-maven-michaelallen" at "https://dl.bintray.com/michaelallen/maven/"

// sbt-mustache
addSbtPlugin("io.michaelallen.mustache" % "sbt-mustache" % "0.2")
