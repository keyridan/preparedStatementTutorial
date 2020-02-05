package com.j0rsa.tutorial.preparedStatement

import com.typesafe.config.ConfigFactory
import io.github.config4k.extract

data class DbConfig(
    val url: String,
    val driver: String
)

object Config {
    val db: DbConfig = ConfigFactory.load().extract("db")
}

