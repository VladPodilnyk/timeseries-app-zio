package timeseries.repo

import timeseries.model.{UserRequest, ValueWithTimestamp}
import zio.{Task, ZIO, ZLayer}

import java.time.ZonedDateTime

trait TimeSeries:
  def submit(timestamp: ZonedDateTime, value: Double): Task[Unit]
  def fetch(iterator: UserRequest, pageLimit: Int): Task[List[ValueWithTimestamp]]

object TimeSeries:
  // TODO: do I need layer if a class doesn't require any dependency
  val test = ZLayer.succeed(TimeSeriesStub())

final class TimeSeriesStub() extends TimeSeries:
  private[this] val state = new scala.collection.concurrent.TrieMap[ZonedDateTime, Double]()

  override def submit(timestamp: ZonedDateTime, value: Double): Task[Unit] = ZIO.attempt(state.put(timestamp, value))

  override def fetch(iterator: UserRequest, pageLimit: Int): Task[List[ValueWithTimestamp]] =
    ZIO.attempt {
      state
        .filter {
          case (timestamp, _) =>
            timestamp.toEpochSecond <= iterator.end.toEpochSecond && timestamp.toEpochSecond >= iterator.start.toEpochSecond
        }.toList
        .sortBy(_._1)
        .take(pageLimit)
        .map { case (timestamp, value) => ValueWithTimestamp(value.toFloat, timestamp) }
    }

// object TimeSeries {
//   final class DummyImpl[F[+_, +_]: IO2] extends TimeSeries[F] {
//     private[this] val state = new scala.collection.concurrent.TrieMap[ZonedDateTime, Double]()

//     override def submit(timestamp: ZonedDateTime, value: Double): F[Throwable, Unit] = {
//       F.syncThrowable {
//         state.put(timestamp, value)
//       }.void
//     }

//     override def fetch(iterator: UserRequest, pageLimit: Int): F[Throwable, List[ValueWithTimestamp]] = {
//       F.syncThrowable {
//         state
//           .filter {
//             case (timestamp, _) =>
//               timestamp.toEpochSecond <= iterator.end.toEpochSecond && timestamp.toEpochSecond >= iterator.start.toEpochSecond
//           }.toList
//           .sortBy(_._1)
//           .take(pageLimit)
//           .map { case (timestamp, value) => ValueWithTimestamp(value.toFloat, timestamp) }
//       }
//     }
//   }

//   final class PostgresImpl[F[+_, +_]: Monad2](
//     sql: SQL[F],
//     log: LogIO2[F],
//   ) extends Lifecycle.LiftF[F[Throwable, _], TimeSeries[F]](for {
//       _ <- log.info(s"Creating Ladder table ${log -> "LambdaFunction"} with $log")
//       _ <- sql.execute("timeseries-ddl") {
//         sql"""create table if not exists timeseries (
//              | id bigserial not null,
//              | time_mark timestamptz not null,
//              | value float not null,
//              | primary key (id)
//              |) without oids;
//              |create index if not exists time_mark_idx on timeseries (time_mark);
//              |""".stripMargin.update.run
//       }

//       res = new TimeSeries[F] {
//         override def submit(timestamp: ZonedDateTime, value: Double): F[Throwable, Unit] = {
//           sql
//             .execute("submit-value") {
//               sql"""insert into timeseries (id, time_mark, value)
//                    |values (default, $timestamp, $value)
//                  """.stripMargin.update.run
//             }.void
//         }

//         override def fetch(iterator: UserRequest, pageLimit: Int): F[Throwable, List[ValueWithTimestamp]] = {
//           sql
//             .execute("fetch-values") {
//               sql"""select time_mark, value from timeseries
//                    |where time_mark >= ${iterator.start} and time_mark <= ${iterator.end}
//                    |order by time_mark
//                    |limit $pageLimit
//                  """.stripMargin.query[(ZonedDateTime, Double)].to[List]
//             }.map(_.map { case (timeMark, value) => ValueWithTimestamp(value.toFloat, timeMark) })
//         }
//       }
//     } yield res)
// }
