package timeseries.domain

import com.google.protobuf.timestamp.Timestamp

import java.time.{Instant, ZoneId, ZonedDateTime}

object utils:
  extension (timestamp: Timestamp)
    def toZonedDateTime: ZonedDateTime =
      val instant = Instant.ofEpochSecond(timestamp.seconds, timestamp.nanos.toLong)
      instant.atZone(ZoneId.of("UTC"))

  extension (timestamp: ZonedDateTime) def toTimestamp: Timestamp = Timestamp(timestamp.toEpochSecond)
