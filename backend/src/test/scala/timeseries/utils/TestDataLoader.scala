//package timeseries.utils
//
//import izumi.functional.bio.{F, IO2}
//import izumi.fundamentals.platform.time.IzTime
//import timeseries.repo.TimeSeries
//
//import java.time.ZonedDateTime
//import scala.util.Random
//
//final class TestDataLoader[F[+_, +_]: IO2](repo: TimeSeries[F]) {
//  def load(): F[Throwable, ZonedDateTime] = {
//    val rnd = new Random()
//    for {
//      now       <- F.sync(rndTimeMark(rnd))
//      rndDates  <- F.traverse(1L to 10L)(v => F.sync(now.plusHours(v)))
//      rndValues <- F.traverse(1 to 10)(_ => F.sync(rnd.nextInt(20)))
//      _ <- F.traverse(rndDates.zip(rndValues)) {
//        case (date, value) => repo.submit(date, value.toDouble)
//      }
//    } yield now
//  }
//
//  private[this] def rndTimeMark(rnd: Random): ZonedDateTime = {
//    val year  = rnd.between(1900, 2100)
//    val month = rnd.between(1, 12)
//    val day   = rnd.between(1, 28)
//    val hour  = rnd.between(0, 23)
//    val min   = rnd.between(0, 59)
//    val sec   = rnd.between(0, 59)
//    ZonedDateTime.of(year, month, day, hour, min, sec, 0, IzTime.TZ_UTC)
//  }
//}
