package com.j0rsa.tutorial.preparedStatement

import org.jetbrains.exposed.dao.IntIdTable

object Users : IntIdTable("users", "id") {
    val name = varchar("name", 50)
}