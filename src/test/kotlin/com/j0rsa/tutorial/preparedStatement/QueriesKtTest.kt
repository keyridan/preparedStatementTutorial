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
            val resultSet: List<String> =
                """SELECT * FROM Users""".trimIndent()
                    .exec()
                    .getValue("name")

            assertThat(resultSet).containsOnly("user1", "user2")
        }
    }

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