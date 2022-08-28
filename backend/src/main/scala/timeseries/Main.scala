package timeseries

import timeseries.api.{GrpcServerApi, HttpApi}
import timeseries.configs.*
import timeseries.configs.ServerConfig.*
import timeseries.domain.DataFetcher
import timeseries.grpc.{GrpcClient, GrpcServer}
import timeseries.http.HttpServer
import timeseries.repo.TimeSeries
import timeseries.sql.SQL
import timeseries.utils.CsvDataLoader
import zio.*
import zio.logging.backend.SLF4J
import zio.logging.removeDefaultLoggers

object Main extends ZIOAppDefault:
  override def run =
    ZIO
      .serviceWithZIO[ServiceLauncher](_.start)
      .provide(
        ServiceLauncher.layer,
        GrpcServer.layer,
        GrpcClient.live,
        GrpcServerApi.layer,
        HttpServer.layer,
        HttpApi.live,
        DataFetcher.layer,
        TimeSeries.live,
        SQL.layer,
        CsvDataLoader.layer,
        ZLayer.succeed(HttpServerConfig("0.0.0.0", 8080)),
        ZLayer.succeed(GrpcServerConfig("0.0.0.0", 8081)),
        ZLayer.succeed(LimitsCfg(512)),
        ZLayer.succeed(PostgresPortCfg("0.0.0.0", 5432)),
        ZLayer.succeed(
          PostgresCfg(
            jdbcDriver = "org.postgresql.Driver",
            url        = "jdbc:postgresql://{host}:{port}/postgres",
            user       = "postgres",
            password   = "postgres",
          )
        ),
        SLF4J.slf4j,
        removeDefaultLoggers,
      )
