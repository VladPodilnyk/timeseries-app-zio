package timeseries.sql

import cats.implicits.*
import cats.effect.Blocker
import doobie.free.connection.ConnectionIO
import doobie.hikari.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import timeseries.configs.{PostgresCfg, PostgresPortCfg}
import timeseries.model.QueryFailure
import zio.*
import zio.interop.catz.*

import java.net.{InetAddress, InetSocketAddress, Socket}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.*

trait SQL:
  def execute[A](queryName: String)(conn: ConnectionIO[A]): Task[A]

object SQL:
  def integrationCheck(host: String, port: Int): Either[Throwable, Unit] =
    try
      val address = new InetSocketAddress(host, port)
      try
        val socket = new Socket()
        try
          socket.connect(address, 10000)
          Right(())
        finally socket.close()
      catch
        case t: Throwable =>
          val message = s"DB unavailable on ${address.getHostName}:${address.getPort}, details: ${t.getMessage}"
          Left(new RuntimeException(message))
    catch
      case t: Throwable =>
        val message = s"Unknown address, details: ${t.getMessage}"
        Left(new RuntimeException(message))

  val layer = ZLayer.scoped {
    given rn: Runtime[Any] = Runtime.default
    val ec                 = Runtime.defaultExecutor.asExecutionContext
    for
      portConfig <- ZIO.service[PostgresPortCfg]
      config     <- ZIO.service[PostgresCfg]
      transactor <- HikariTransactor
        .newHikariTransactor[Task](
          driverClassName = config.jdbcDriver,
          url             = portConfig.substitute(config.url),
          user            = config.user,
          pass            = config.password,
          connectEC       = ec,
          blocker         = Blocker.liftExecutionContext(ec),
        ).toScoped[Any]
      _ <- ZIO.fromEither(integrationCheck(portConfig.host, portConfig.port))
    yield SQLImpl(transactor)
  }

final class SQLImpl(transactor: Transactor[Task]) extends SQL:
  override def execute[A](queryName: String)(conn: ConnectionIO[A]): Task[A] =
    transactor.trans.apply(conn)
