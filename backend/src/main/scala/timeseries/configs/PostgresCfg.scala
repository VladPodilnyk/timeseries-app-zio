package timeseries.configs

final case class PostgresCfg(
  jdbcDriver: String,
  url: String,
  user: String,
  password: String,
)
