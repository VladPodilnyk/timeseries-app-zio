// package timeseries

// import izumi.functional.bio.{Clock2, F}
// import timeseries.domain.DataFetcher
// import timeseries.model.UserRequest
// import zio.IO

// final class DataFetcherTest extends WithDummyGrpc with DummyTest {
//   "DataFetcher" should {
//     "validate user requests" in {
//       (dataFetcher: DataFetcher[IO], clock: Clock2[IO]) =>
//         for {
//           now       <- clock.now()
//           request    = UserRequest(now, now.plusHours(1L))
//           badRequest = UserRequest(now.plusHours(2L), now)
//           // goes fine
//           _        <- dataFetcher.retrieve(request)
//           isFailed <- dataFetcher.retrieve(badRequest).map(_ => false).catchAll(_ => F.pure(true))
//           _        <- assertIO(isFailed)
//         } yield ()
//     }
//   }
// }
