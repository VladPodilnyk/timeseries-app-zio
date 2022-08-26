package timeseries.api

import timeseries.domain.DataFetcher
import timeseries.model.UserRequest
import zhttp.http.*
import zio.*
import zio.json.*

trait HttpApi:
  def http: Http[Any, Throwable, Request, Response]

object HttpApi:
  val live = ZLayer.fromFunction(TimeseriesServiceApi(_))

final class TimeseriesServiceApi(dataFetcher: DataFetcher) extends HttpApi:
  override def http = Http.collectHttp[Request] {
    case req @ Method.GET -> !! / "timeseries" =>
      Http.fromResource("index.html").orElse(Http.notFound).as(Response.ok)

    case req @ Method.POST -> !! / "fetch" =>
      Http.fromZIO {
        for {
          userRequest <- parseBody[UserRequest](req)
          result      <- dataFetcher.retrieve(userRequest)
        } yield Response.json(result.toJson)
      }
  }

  private def parseBody[A: JsonDecoder](request: Request): Task[A] =
    for {
      body   <- request.bodyAsString.orElseFail(RuntimeException("Better errors comming soon."))
      parsed <- ZIO.from(body.fromJson[A]).mapError(_ => RuntimeException("He is gonna back. Whait a minute."))
    } yield parsed
