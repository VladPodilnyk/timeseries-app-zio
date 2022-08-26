package timeseries.api

import com.google.protobuf.timestamp.Timestamp
import shop.rpc.timeseries_service
import shop.rpc.timeseries_service.{DataWithTimestamp, QueryResponse, TimeSeriesGrpc}
import timeseries.configs.LimitsCfg
import timeseries.domain.utils.*
import timeseries.model.UserRequest
import timeseries.repo.TimeSeries
import zio.*

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

trait GrpcServerApi extends TimeSeriesGrpc.TimeSeries:
  def fetchData(request: timeseries_service.Iterator): Future[QueryResponse]

object GrpcServerApi:
  val layer = ZLayer.fromZIO {
    for {
      timeSeries <- ZIO.service[TimeSeries]
      config     <- ZIO.service[LimitsCfg]
    } yield GrpcServiceApiImpl(timeSeries, config)
  }

class GrpcServiceApiImpl(repo: TimeSeries, limitsCfg: LimitsCfg) extends GrpcServerApi:
  override def fetchData(request: timeseries_service.Iterator): Future[QueryResponse] =
    val dataRequest = UserRequest(request.start.toZonedDateTime, request.end.toZonedDateTime)
    val eff         = repo.fetch(dataRequest, limitsCfg.pageLimit)

    Unsafe.unsafe {
      implicit unsafe =>
        Runtime.default.unsafe
          .runToFuture(eff).map {
            values =>
              val last           = values.lastOption.map(v => Timestamp(v.timestamp.toEpochSecond))
              val protobufModels = values.map(v => DataWithTimestamp(v.value, Timestamp(v.timestamp.toEpochSecond)))
              QueryResponse(last, protobufModels)
          }(global)
    }
