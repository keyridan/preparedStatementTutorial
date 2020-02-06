package com.j0rsa.tutorial.preparedStatement

import assertk.assertThat
import assertk.assertions.containsOnly
import com.j0rsa.tutorial.preparedStatement.ResultColumn.IntColumn
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
            val resultSet: List<User> =
                """SELECT * FROM Users""".trimIndent()
                    .exec()
                    .toEntities()
            assertThat(resultSet.map { it.name }).containsOnly("user1", "user2")
        }
    }

    @Test
    fun testExecQuery() {
        tempTx {
            insertUser("user1")
            insertUser("user2")
            val resultSet: List<UserData> =
                """
                SELECT 
                    id, 
                    name, 
                    row_number() over(order by id) rn
                FROM Users
                """.trimIndent()
                    .exec()
                    .map { result: ResultSet ->
                        UserData(
                            user = result.toEntity(),
                            rowNumber = IntColumn("rn") from result
                        )
                    }
            assertThat(resultSet.map { it.user.name }).containsOnly("user1", "user2")
            assertThat(resultSet.map { it.rowNumber }).containsOnly(1, 2)
        }
    }

    data class User(val id: Int, val name: String)
    data class UserData(val user: User, val rowNumber: Int)

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