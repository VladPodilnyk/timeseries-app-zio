package timeseries

import timeseries.configs.{PostgresCfg, PostgresPortCfg}
import timeseries.model.UserRequest
import timeseries.repo.TimeSeries
import timeseries.sql.SQL
import timeseries.utils.TestDataLoader
import zio.*
import zio.test.*

object TimeSeriesTest extends ZIOSpecDefault:
  def repoTest =
    for
      repo   <- ZIO.service[TimeSeries]
      loader <- ZIO.service[TestDataLoader]
      now    <- loader.load()
      res1   <- repo.fetch(UserRequest(now, now.plusHours(6L)), 10)
      res2   <- repo.fetch(UserRequest(now, now.plusHours(6L)), 2)
    yield assertTrue(res1.size == 6, res2.size == 2)

  def spec = suite("TimeSeries spec")(
    test("dummy submit & fetch")(repoTest)
      .provide(TimeSeries.test, TestDataLoader.layer),
    test("prod submit & fetch")(repoTest)
      .provide(
        TimeSeries.live,
        TestDataLoader.layer,
        SQL.layer,
        ZLayer.succeed(PostgresPortCfg("localhost", 5432)),
        ZLayer.succeed(
          PostgresCfg(
            jdbcDriver = "org.postgresql.Driver",
            url        = "jdbc:postgresql://{host}:{port}/postgres",
            user       = "postgres",
            password   = "postgres",
          )
        ),
      ),
  )
