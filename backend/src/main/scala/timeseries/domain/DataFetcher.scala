package timeseries.domain

import com.google.protobuf.timestamp.Timestamp
import shop.rpc.timeseries_service
import timeseries.domain.utils.*
import timeseries.grpc.GrpcClient
import timeseries.model.{PagedData, UserRequest, ValueWithTimestamp}
import zio.{Task, ZIO, ZLayer}

final class DataFetcher(client: GrpcClient):
  def retrieve(query: UserRequest): Task[PagedData] =
    for
      iterator      <- validateRequest(query)
      queryResponse <- client.fetchData(iterator)
      results        = queryResponse.data.map(v => ValueWithTimestamp(v.value, v.receivedTime.toZonedDateTime)).toList
    yield PagedData(queryResponse.lastProcessed.map(_.toZonedDateTime), results)

  private[this] def validateRequest(request: UserRequest): Task[timeseries_service.Iterator] =
    val start = request.start.toEpochSecond
    val end   = request.end.toEpochSecond
    ZIO
      .when(start > end)(
        ZIO.fail(
          new RuntimeException(s"Invalid iterator error. Start time greater than end time: start=${request.start}, end=${request.end}")
        )
      ).as(timeseries_service.Iterator(Timestamp(start), Timestamp(end)))

object DataFetcher:
  val layer = ZLayer.fromFunction(DataFetcher(_))

