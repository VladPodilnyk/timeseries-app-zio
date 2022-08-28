package timeseries.grpc

import io.grpc.{Server, ServerBuilder}
import shop.rpc.timeseries_service.TimeSeriesGrpc
import timeseries.api.GrpcServerApi
import timeseries.configs.ServerConfig.GrpcServerConfig
import zio.*

import scala.concurrent.ExecutionContext

final case class GrpcServer(value: Server)

object GrpcServer:
  def acquire(api: GrpcServerApi, config: GrpcServerConfig): ZIO[Scope, Throwable, Server] =
    ZIO.acquireRelease {
      ZIO.attempt {
        ServerBuilder
          .forPort(config.port)
          .addService(TimeSeriesGrpc.bindService(api, ExecutionContext.global))
          .build()
          .start()
      }
    }(server => ZIO.succeed(server.shutdown()))

  val layer = ZLayer.scoped {
    for
      serverApi <- ZIO.service[GrpcServerApi]
      config    <- ZIO.service[GrpcServerConfig]
      server    <- acquire(serverApi, config)
    yield GrpcServer(server)
  }
