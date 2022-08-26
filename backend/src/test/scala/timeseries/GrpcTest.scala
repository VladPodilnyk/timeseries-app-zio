// package timeseries

// import com.google.protobuf.timestamp.Timestamp
// import shop.rpc.timeseries_service
// import timeseries.grpc.{GrpcClient, GrpcServer}
// import timeseries.utils.TestDataLoader
// import zio.IO

// final class GrpcTest extends WithTestDataLoader with DummyTest {
//   "gRPC service" should {
//     "send data" in {
//       (grpcServerResource: GrpcServer[IO], client: GrpcClient[IO], loader: TestDataLoader[IO]) =>
//         grpcServerResource.resource.use {
//           _ =>
//             for {
//               start   <- loader.load()
//               begin    = start.toEpochSecond
//               end      = start.plusHours(2L).toEpochSecond
//               iterator = timeseries_service.Iterator(Timestamp(begin), Timestamp(end))
//               res     <- client.fetchData(iterator)
//               _       <- assertIO(res.lastProcessed.isDefined && res.data.nonEmpty)
//             } yield ()
//         }
//     }
//   }
// }
