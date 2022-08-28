package timeseries.http

import timeseries.api.HttpApi
import timeseries.configs.ServerConfig.HttpServerConfig
import zhttp.http.Middleware
import zhttp.service.Server
import zio.*

import java.net.InetAddress
import scala.concurrent.ExecutionContext

final class HttpServer(api: HttpApi, config: HttpServerConfig):
  def start = Server.start(config.port, api.http)

object HttpServer:
  val layer = ZLayer.fromFunction(HttpServer(_, _))
