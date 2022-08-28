package timeseries

import timeseries.grpc.GrpcServer
import timeseries.http.HttpServer
import timeseries.utils.CsvDataLoader
import zio.Console.*
import zio.ZLayer

import scala.annotation.unused

final class ServiceLauncher(httpServer: HttpServer, @unused rpcServer: GrpcServer, dataLoader: CsvDataLoader):
  def start =
    for
      _ <- printLine("Servers are about to start")
      _ <- dataLoader.load("src/main/resources/meterusage.csv")
      // _ <- grpcServer.start
      // _ <- printLine("Grpc server has been started.")
      _ <- httpServer.start
      _ <- printLine("Http server has been started")
    yield ()

object ServiceLauncher:
  val layer = ZLayer.fromFunction(ServiceLauncher(_, _, _))
