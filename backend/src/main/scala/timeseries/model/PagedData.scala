package timeseries.model

import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time.ZonedDateTime

final case class PagedData(lastTimestamp: Option[ZonedDateTime], values: List[ValueWithTimestamp])
object PagedData:
  given encoder: JsonCodec[PagedData] = DeriveJsonCodec.gen[PagedData]
