import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val bootstrapVersion = "8.4.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc" %% "play-frontend-hmrc-play-30"         % "8.4.0",
    "uk.gov.hmrc" %% "bootstrap-frontend-play-30" % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "org.jsoup"                     % "jsoup"                   % "1.15.3",
    "uk.gov.hmrc"                  %% "bootstrap-test-play-30"  % bootstrapVersion

  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
