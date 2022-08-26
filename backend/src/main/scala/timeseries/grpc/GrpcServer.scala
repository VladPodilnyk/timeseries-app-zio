package timeseries.grpc

import io.grpc.{Server, ServerBuilder}
import shop.rpc.timeseries_service.TimeSeriesGrpc
import timeseries.api.GrpcServerApi
import timeseries.configs.GrpcServerCfg
import zio.{ZIO, ZLayer}

import scala.concurrent.ExecutionContext

final class GrpcServer(grpcServerApi: GrpcServerApi, config: timeseries.configs.GrpcServerCfg):
  def start =
    ZIO.acquireRelease {
      ZIO.attempt {
        ServerBuilder
          .forPort(config.port)
          .addService(TimeSeriesGrpc.bindService(grpcServerApi, ExecutionContext.global))
          .build()
          .start()
      }
    }(server => ZIO.succeedUnsafe(_ => server.shutdown())).unit

object GrpcServer:
  val layer = ZLayer.fromFunction(GrpcServer(_, _))
