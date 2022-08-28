package timeseries

import timeseries.domain.DataFetcher
import timeseries.grpc.GrpcClient
import timeseries.model.UserRequest
import zio.*
import zio.Random.RandomScala
import zio.test.*

object DataFetcherTest extends ZIOSpecDefault:
  def spec = suite("DataFetcherTests")(
    test("validate user request") {
      for
        now         <- Clock.currentDateTime.map(_.toZonedDateTime)
        request      = UserRequest(now, now.plusHours(1L))
        badRequest   = UserRequest(now.plusHours(2L), now)
        dataFetcher <- ZIO.service[DataFetcher]
        _           <- dataFetcher.retrieve(request)
        isFailed    <- dataFetcher.retrieve(badRequest).map(_ => false).catchAll(_ => ZIO.succeed(true))
      yield assertTrue(isFailed)
    }
  ).provide(
    DataFetcher.layer,
    GrpcClient.test,
    ZLayer.succeed(RandomScala(scala.util.Random()))
  )
