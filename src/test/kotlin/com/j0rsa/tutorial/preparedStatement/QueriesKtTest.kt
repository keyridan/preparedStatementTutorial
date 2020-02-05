package com.j0rsa.tutorial.preparedStatement

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.j0rsa.tutorial.preparedStatement.TransactionManager.currentTransaction
import com.j0rsa.tutorial.preparedStatement.TransactionManager.tx
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.junit.jupiter.api.Test
import java.sql.ResultSet

internal class QueriesKtTest {

    @Test
    fun testExecSimpleQuery() {
        tempTx {
            insertUser("user1")
            insertUser("user2")
            val resultSet: ResultSet? =
                """SELECT * FROM Users""".trimIndent()
                    .exec()

            assertThat(resultSet).isNotNull()
            resultSet!!.next()
            assertThat(resultSet.getString("name")).isEqualTo("user1")
            resultSet.next()
            assertThat(resultSet.getString("name")).isEqualTo("user2")
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