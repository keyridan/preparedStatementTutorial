package com.j0rsa.tutorial.preparedStatement

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.j0rsa.tutorial.preparedStatement.TransactionManager.tx
import org.junit.jupiter.api.Test
import java.sql.ResultSet

internal class QueriesKtTest {

    @Test
    fun testExecSimpleQuery() {
        tx {
            val resultSet: ResultSet? =
                """SELECT 'it_works!' v FROM DUAL""".trimIndent()
                    .exec()

            assertThat(resultSet).isNotNull()
            resultSet!!.next()
            assertThat(resultSet.getString("v")).isEqualTo("it_works!")
        }
    }
}