import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"             % "3.21.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"     % "6.2.0",
    "uk.gov.hmrc"       %% "play-language"                  % "5.2.0-play-28"
  )

  val test = Seq(
    "org.scalatest"                %% "scalatest"               % "3.2.12",
    "org.scalatestplus"            %% "scalacheck-1-15"         % "3.2.11.0",
    "org.scalatestplus"            %% "mockito-3-4"             % "3.2.10.0",
    "org.scalatestplus.play"       %% "scalatestplus-play"      % "5.1.0",
    "org.pegdown"                  %  "pegdown"                 % "1.6.0",
    "org.jsoup"                    %  "jsoup"                   % "1.14.3",
    "com.typesafe.play"            %% "play-test"               % PlayVersion.current,
    "org.mockito"                  %% "mockito-scala"           % "1.17.7",
    "org.scalacheck"               %% "scalacheck"              % "1.16.0",
    "com.github.tomakehurst"       %  "wiremock-jre8"           % "2.33.2",
    "com.vladsch.flexmark"         %  "flexmark-all"            % "0.62.2",
  "com.fasterxml.jackson.module"   %% "jackson-module-scala"    % "2.13.3"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
