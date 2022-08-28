package timeseries.api

import timeseries.domain.DataFetcher
import timeseries.model.UserRequest
import zhttp.http.*
import zio.*
import zio.stream.*
import zio.json.*

import java.nio.file.Paths

trait HttpApi:
  def http: Http[Any, Throwable, Request, Response]

object HttpApi:
  val live = ZLayer.fromFunction(TimeseriesServiceApi(_))

final class TimeseriesServiceApi(dataFetcher: DataFetcher) extends HttpApi:
  override def http = Http.collectHttp[Request] {
    case Method.GET -> !! => Http.text("Hello, please visit /timeseries app and test a service!")

    case req @ Method.GET -> !! / "timeseries" =>
      Http.fromStream(ZStream.fromPath(Paths.get("src/main/resources/index.html")))

    case req @ Method.POST -> !! / "fetch" =>
      Http.fromZIO {
        for
          userRequest <- parseBody[UserRequest](req)
          result      <- dataFetcher.retrieve(userRequest)
        yield Response.json(result.toJson)
      }
  }

  private def parseBody[A: JsonDecoder](request: Request): Task[A] =
    for
      body   <- request.bodyAsString.orElseFail(RuntimeException("Better errors comming soon."))
      parsed <- ZIO.from(body.fromJson[A]).orElseFail(RuntimeException("He is gonna back. Whait a minute."))
    yield parsed
