package timeseries.utils

import timeseries.repo.TimeSeries
import zio.Task
import zio.ZIO
import zio.ZLayer
import zio.stream
import zio.stream.*

import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext
import scala.util.Try
import timeseries.repo.TimeSeries

final class CsvDataLoader(repo: TimeSeries):
  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  def load(path: String): Task[Unit] = {
    ZStream
      .fromFile(Paths.get(path).toFile, 4096)
      .via(ZPipeline.utf8Decode >>> ZPipeline.splitLines)
      .drop(1) // drop header
      .map(_.split(',').toList) // split every row on columns
      .dropWhile(_.isEmpty)
      .map(parseRow)
      .mapZIO {
        case None => ZIO.unit
        case Some((time, value)) => repo.submit(time, value)
      }
      .runDrain
  }

  private[this] def parseRow(row: List[String]): Option[(ZonedDateTime, Double)] = {
    row match {
      case timestamp :: value :: Nil =>
        Try {
          val timeMark    = LocalDateTime.parse(timestamp, formatter).atZone(ZoneId.of("UTC"))
          val doubleValue = value.toDouble
          timeMark -> doubleValue
        }.toOption
      case _ => None
    }
  }

object CsvDataLoader:
  val layer = ZLayer.fromFunction(CsvDataLoader(_))
