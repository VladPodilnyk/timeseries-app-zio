package timeseries.model

import zio.json.{DeriveJsonCodec, JsonCodec}

import java.time.ZonedDateTime

final case class UserRequest(start: ZonedDateTime, end: ZonedDateTime)
object UserRequest:
  given codec: JsonCodec[UserRequest] = DeriveJsonCodec.gen[UserRequest]
