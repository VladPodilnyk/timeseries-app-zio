package timeseries.grpc

import io.grpc.ManagedChannelBuilder
import shop.rpc.timeseries_service
import shop.rpc.timeseries_service.{DataWithTimestamp, QueryResponse, TimeSeriesGrpc}
import timeseries.configs.ServerConfig.GrpcServerConfig
import zio.*
import zio.Random.RandomScala

trait GrpcClient:
  def fetchData(iterator: timeseries_service.Iterator): Task[QueryResponse]

object GrpcClient:
  val live = ZLayer.fromFunction(GrpcClientImpl(_))
  val test = ZLayer.fromFunction(DummyImpl(_))

final class DummyImpl(rnd: RandomScala) extends GrpcClient:
  override def fetchData(iterator: timeseries_service.Iterator): Task[QueryResponse] =
    rnd.nextIntBounded(30).map {
      value => QueryResponse(Some(iterator.start), List(DataWithTimestamp(value.toFloat, iterator.start)))
    }

final class GrpcClientImpl(grpcServerCfg: GrpcServerConfig) extends GrpcClient:
  import grpcServerCfg.*
  override def fetchData(iterator: timeseries_service.Iterator): Task[QueryResponse] =
    val channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build
    val stub    = TimeSeriesGrpc.stub(channel)
    ZIO.fromFuture(_ => stub.fetchData(iterator))
