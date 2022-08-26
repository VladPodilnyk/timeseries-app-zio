package timeseries

import timeseries.http.HttpServer
import timeseries.grpc.GrpcServer
import zio.ZLayer
import timeseries.utils.CsvDataLoader

final class ServiceLauncher(httpServer: HttpServer, grpcServer: GrpcServer, dataLoader: CsvDataLoader):
  def start =
    for {
      _ <- httpServer.start
      //_ <- grpcServer.start
      //_ <- dataLoader.load("src/main/resources/meterusage.csv")
    } yield ()

object ServiceLauncher:
  val layer = ZLayer.fromFunction(ServiceLauncher(_, _, _))
