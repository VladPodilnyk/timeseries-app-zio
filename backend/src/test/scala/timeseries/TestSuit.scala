// package timeseries

// import distage.{DIKey, ModuleDef, Scene}
// import izumi.distage.model.definition.Activation
// import izumi.distage.model.definition.StandardAxis.Repo
// import izumi.distage.plugins.PluginConfig
// import izumi.distage.testkit.TestConfig
// import izumi.distage.testkit.scalatest.{AssertIO2, Spec2}
// import timeseries.grpc.GrpcClient
// import timeseries.repo.TimeSeries
// import timeseries.utils.TestDataLoader
// import zio.IO

// abstract class TestSuit extends Spec2[IO] with AssertIO2[IO] {
//   override def config = super.config.copy(
//     pluginConfig = PluginConfig.cached(packagesEnabled = Seq("timeseries.plugins")),
//     activation = Activation(Scene -> Scene.Managed),
//     memoizationRoots = Set(
//       DIKey[TimeSeries[IO]]
//     ),
//     configBaseName = "timeseries-test",
//   )
// }

// trait DummyTest extends TestSuit {
//   override final def config = super.config.copy(
//     activation = super.config.activation ++ Activation(Repo -> Repo.Dummy)
//   )
// }

// trait ProdTest extends TestSuit {
//   override final def config = super.config.copy(
//     activation = super.config.activation ++ Activation(Repo -> Repo.Prod)
//   )
// }

// trait WithTestDataLoader extends TestSuit {
//   override def config: TestConfig = super.config.copy(
//     moduleOverrides = super.config.moduleOverrides ++ new ModuleDef {
//       make[TestDataLoader[zio.IO]]
//     }
//   )
// }

// trait WithDummyGrpc extends TestSuit {
//   override def config: TestConfig = super.config.copy(
//     moduleOverrides = super.config.moduleOverrides overriddenBy new ModuleDef {
//       make[GrpcClient[IO]].from[GrpcClient.DummyImpl[IO]]
//     }
//   )
// }
