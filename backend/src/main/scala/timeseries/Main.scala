package timeseries

import zio.*
//import zio.logging.backend.SLF4J
//import zio.logging.removeDefaultLoggers
import timeseries.http.HttpServer
import timeseries.grpc.GrpcServer
import timeseries.api.HttpApi
import timeseries.domain.DataFetcher
import timeseries.api.GrpcServerApi
import timeseries.repo.TimeSeries
import timeseries.configs.GrpcServerCfg
import timeseries.configs.LimitsCfg
import timeseries.utils.CsvDataLoader

object Main extends ZIOAppDefault:
  override def run =
    ZIO
      .serviceWithZIO[ServiceLauncher](_.start)
    //(launcher => ZIO.scoped(launcher.start))
      .provide(
        ServiceLauncher.layer,
        GrpcServer.layer,
        GrpcServerApi.layer,
        HttpServer.layer,
        HttpApi.live,
        DataFetcher.layer,
        TimeSeries.test,
        CsvDataLoader.layer,
        ZLayer.succeed(GrpcServerCfg("localhost", 8080)),
        ZLayer.succeed(LimitsCfg(512)),
        //SLF4J.slf4j,
        //removeDefaultLoggers,
      )
