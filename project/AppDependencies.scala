import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "0.58.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "5.16.0",
    "uk.gov.hmrc"       %% "play-language"                  % "4.12.0-play-28"
  )

  val test = Seq(
    "org.scalatest"           %% "scalatest"               % "3.2.7",
    "org.scalatestplus"       %% "scalacheck-1-15"         % "3.2.7.0",
    "org.scalatestplus"       %% "mockito-3-4"             % "3.2.7.0",
    "org.scalatestplus.play"  %% "scalatestplus-play"      % "5.1.0",
    "org.pegdown"             %  "pegdown"                 % "1.6.0",
    "org.jsoup"               %  "jsoup"                   % "1.13.1",
    "com.typesafe.play"       %% "play-test"               % PlayVersion.current,
    "org.mockito"             %% "mockito-scala"           % "1.16.0",
    "org.scalacheck"          %% "scalacheck"              % "1.15.3",
    "com.github.tomakehurst"  %  "wiremock-jre8"           % "2.23.2",
    "com.vladsch.flexmark"    %  "flexmark-all"            % "0.35.10" // Required to stay at this version - see https://github.com/scalatest/scalatest/issues/1736
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
