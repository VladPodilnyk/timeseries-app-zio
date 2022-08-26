package timeseries.http

import timeseries.api.HttpApi
import zhttp.http.Middleware
import zhttp.service.Server
import zio.{System, ZLayer}

import scala.concurrent.ExecutionContext

final class HttpServer(api: HttpApi):
  def start =
    for {
      // TODO: pass inet address via config?
      port <- System.envOrElse("PORT", "8080").map(_.toInt)
      _    <- Server.start(port, api.http @@ Middleware.cors())
    } yield ()

object HttpServer:
  val layer = ZLayer.fromFunction(HttpServer(_))
