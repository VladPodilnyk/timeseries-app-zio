package timeseries.configs

trait ServerConfig:
  def host: String
  def port: Int

object ServerConfig:
  final case class HttpServerConfig(host: String, port: Int) extends ServerConfig
  final case class GrpcServerConfig(host: String, port: Int) extends ServerConfig
