package timeseries.model

import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time.ZonedDateTime

final case class ValueWithTimestamp(value: Float, timestamp: ZonedDateTime)
object ValueWithTimestamp:
  given codec: JsonCodec[ValueWithTimestamp] = DeriveJsonCodec.gen[ValueWithTimestamp]
