// package timeseries

// import io.circe.parser
// import io.circe.syntax.*
// import izumi.functional.bio.{Clock2, F}
// import timeseries.http.HttpServer
// import timeseries.model.{PagedData, UserRequest}
// import zio.IO

// final class HttpApiTest extends WithDummyGrpc with DummyTest {
//   "Http API" should {
//     "retrieve request and send to gRPC backend" in {
//       (server: HttpServer.Impl[IO], clock: Clock2[IO]) =>
//         server.resource.use {
//           _ =>
//             for {
//               now        <- clock.now()
//               requestData = UserRequest(now, now.plusHours(1L)).asJson.noSpaces
//               res        <- F.syncThrowable(requests.post("http://localhost:8080/fetch", data = requestData))
//               _          <- assertIO(res.statusCode == 200)
//               _ = parser.decode[PagedData](res.text()) match {
//                 case Left(value)  => fail(value.getMessage)
//                 case Right(value) => assert(value.lastTimestamp.isDefined && value.values.nonEmpty)
//               }
//             } yield ()
//         }
//     }
//   }
// }
