import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val bootstrapVersion = "7.21.0"

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "play-frontend-hmrc"         % "7.29.0-play-28",
    "uk.gov.hmrc" %% "bootstrap-frontend-play-28" % bootstrapVersion
  )

  val test = Seq(
    "org.scalatest"                %% "scalatest"               % "3.2.14",
    "org.scalatestplus"            %% "scalacheck-1-15"         % "3.2.11.0",
    "org.scalatestplus.play"       %% "scalatestplus-play"      % "5.1.0",
    "org.pegdown"                   % "pegdown"                 % "1.6.0",
    "org.jsoup"                     % "jsoup"                   % "1.15.3",
    "com.typesafe.play"            %% "play-test"               % PlayVersion.current,
    "org.mockito"                  %% "mockito-scala-scalatest" % "1.17.12",
    "org.scalacheck"               %% "scalacheck"              % "1.17.0",
    "com.github.tomakehurst"        % "wiremock-jre8"           % "2.35.0",
    "com.vladsch.flexmark"          % "flexmark-all"            % "0.64.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"    % "2.14.0",
    "uk.gov.hmrc"                  %% "bootstrap-test-play-28"  % bootstrapVersion

  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
