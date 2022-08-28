package timeseries.repo

import doobie.syntax.string.*
import doobie.postgres.implicits.*
import timeseries.model.{UserRequest, ValueWithTimestamp}
import timeseries.sql.SQL
import zio.{Task, ULayer, ZIO, ZLayer}

import java.time.ZonedDateTime
import scala.concurrent.TimeoutException

trait TimeSeries:
  def submit(timestamp: ZonedDateTime, value: Double): Task[Unit]
  def fetch(iterator: UserRequest, pageLimit: Int): Task[List[ValueWithTimestamp]]

object TimeSeries:
  val test = ZLayer.succeed(TimeSeriesStub())
  val live = ZLayer.fromZIO {
    for
      sql <- ZIO.service[SQL]
      _   <- ddlUp(sql)
    yield TimeSeriesProd(sql)
  }

  private def ddlUp(sql: SQL): Task[Unit] =
    sql
      .execute("ddl up") {
        sql"""create table if not exists timeseries (
             | id bigserial not null,
             | time_mark timestamptz not null,
             | value float not null,
             | primary key (id)
             |) without oids;
             |create index if not exists time_mark_idx on timeseries (time_mark);
             |""".stripMargin.update.run
      }.unit

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

final class TimeSeriesProd(sql: SQL) extends TimeSeries:
  override def submit(timestamp: ZonedDateTime, value: Double): Task[Unit] =
    sql
      .execute("submit-value") {
        sql"""insert into timeseries(id, time_mark, value)
             |values (default, $timestamp, $value)
             |""".stripMargin.update.run
      }.unit

  override def fetch(iterator: UserRequest, pageLimit: Int): Task[List[ValueWithTimestamp]] =
    sql
      .execute("fetch-values") {
        sql"""select time_mark, value from timeseries
             |where time_mark >= ${iterator.start} and time_mark <= ${iterator.end}
             |order by time_mark
             |limit $pageLimit
         """.stripMargin.query[(ZonedDateTime, Double)].to[List]
      }.map(_.map { case (timeMark, value) => ValueWithTimestamp(value.toFloat, timeMark) })
