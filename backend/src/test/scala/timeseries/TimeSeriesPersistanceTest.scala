// package timeseries

// import timeseries.model.UserRequest
// import timeseries.repo.TimeSeries
// import timeseries.utils.TestDataLoader
// import zio.IO

// final class TimeSeriesTestDummy extends TimeSeriesTest with DummyTest
// final class TimeSeriesTestPostgres extends TimeSeriesTest with ProdTest

// abstract class TimeSeriesTest extends WithTestDataLoader {
//   "TimeSeries" should {
//     "submit & fetch data" in {
//       (repo: TimeSeries[IO], loader: TestDataLoader[IO]) =>
//         for {
//           now <- loader.load()
//           res1 <- repo.fetch(UserRequest(now, now.plusHours(6L)), 10)
//           _    <- assertIO(res1.size == 6)
//           res2 <- repo.fetch(UserRequest(now, now.plusHours(6L)), 2)
//           _    <- assertIO(res2.size == 2)
//         } yield ()
//     }
//   }
// }
