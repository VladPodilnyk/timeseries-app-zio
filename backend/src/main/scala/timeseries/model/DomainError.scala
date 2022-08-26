package timeseries.model

import zio.json.{DeriveJsonEncoder, JsonEncoder}

final case class DomainError(message: String)
object DomainError:
  given codec: JsonEncoder[DomainError] = DeriveJsonEncoder.gen[DomainError]
