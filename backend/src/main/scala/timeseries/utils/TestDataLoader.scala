package timeseries.utils

import timeseries.repo.TimeSeries
import zio.*
import zio.Random.RandomScala

import java.time.{ZoneId, ZonedDateTime}
import scala.util.Random

object TestDataLoader:
  val layer = ZLayer.fromFunction(TestDataLoader(_))

final class TestDataLoader(repo: TimeSeries):
  def load(): Task[ZonedDateTime] =
    val rnd = new Random()
    for
      now       <- ZIO.attemptUnsafe(_ => rndTimeMark(rnd))
      rndDates  <- ZIO.foreach(1L to 10L)(v => ZIO.succeedUnsafe(_ => now.plusHours(v)))
      rndValues <- ZIO.foreach(1 to 10)(_ => ZIO.succeedUnsafe(_ => rnd.nextInt(20)))
      _ <- ZIO.foreach(rndDates.zip(rndValues)) {
        case (date, value) =>
          repo.submit(date, value.toDouble)
      }
    yield now

  private[this] def rndTimeMark(rnd: Random): ZonedDateTime =
    val year  = rnd.between(1900, 2100)
    val month = rnd.between(1, 12)
    val day   = rnd.between(1, 28)
    val hour  = rnd.between(0, 23)
    val min   = rnd.between(0, 59)
    val sec   = rnd.between(0, 59)
    ZonedDateTime.of(year, month, day, hour, min, sec, 0, ZoneId.of("UTC"))
