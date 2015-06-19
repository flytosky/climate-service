name := """climate-service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  javaCore,
  javaJpa,
  "org.springframework" % "spring-context" % "4.1.4.RELEASE",
  "javax.inject" % "javax.inject" % "1",
  "org.springframework.data" % "spring-data-jpa" % "1.7.1.RELEASE",
  "org.springframework" % "spring-expression" % "4.1.4.RELEASE",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.7.Final",
  "org.mockito" % "mockito-core" % "1.10.19" % "test",
  "mysql" % "mysql-connector-java" % "5.1.34"
)


fork in run := false