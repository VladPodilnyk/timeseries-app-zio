package timeseries

import com.google.protobuf.timestamp.Timestamp
import shop.rpc.timeseries_service
import timeseries.api.GrpcServerApi
import timeseries.configs.LimitsCfg
import timeseries.configs.ServerConfig.GrpcServerConfig
import timeseries.grpc.{GrpcClient, GrpcServer}
import timeseries.repo.TimeSeries
import timeseries.utils.TestDataLoader
import zio.*
import zio.Random.RandomScala
import zio.test.*

import java.time.ZonedDateTime

object GrpcTest extends ZIOSpecDefault:
  def spec = suite("gRPC service suite")(
    test("send data") {
      for
        client  <- ZIO.service[GrpcClient]
        loader  <- ZIO.service[TestDataLoader]
        _       <- ZIO.service[GrpcServer]
        start   <- loader.load()
        begin    = start.toEpochSecond
        end      = start.plusHours(2L).toEpochSecond
        iterator = timeseries_service.Iterator(Timestamp(begin), Timestamp(end))
        res     <- client.fetchData(iterator)
      yield assertTrue(res.lastProcessed.isDefined && res.data.nonEmpty)
    }
  ).provide(
    TimeSeries.test,
    GrpcClient.live,
    TestDataLoader.layer,
    GrpcServerApi.layer,
    GrpcServer.layer,
    ZLayer.succeed(GrpcServerConfig("localhost", 8082)),
    ZLayer.succeed(LimitsCfg(512)),
  )
