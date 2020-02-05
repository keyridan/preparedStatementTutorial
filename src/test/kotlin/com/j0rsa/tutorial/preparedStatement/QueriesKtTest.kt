package com.j0rsa.tutorial.preparedStatement

import assertk.assertThat
import assertk.assertions.containsOnly
import com.j0rsa.tutorial.preparedStatement.TransactionManager.currentTransaction
import com.j0rsa.tutorial.preparedStatement.TransactionManager.tx
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.junit.jupiter.api.Test

internal class QueriesKtTest {

    @Test
    fun testExecSimpleQuery() {
        tempTx {
            insertUser("user1")
            insertUser("user2")
            val resultSet: List<User> =
                """SELECT * FROM Users""".trimIndent()
                    .exec()
                    .toEntities()
            assertThat(resultSet.map { it.name }).containsOnly("user1", "user2")
        }
    }

    data class User(val id: Int, val name: String)

    private fun insertUser(name: String = "testUserName") = Users.insert {
        it[this.name] = name
    }

    private fun tempTx(block: () -> Unit) =
        tx {
            createSchema()
            block()
            currentTransaction().rollback()
        }

    private fun createSchema() {
        SchemaUtils.create(Users)
    }

}