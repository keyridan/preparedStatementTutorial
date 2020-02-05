package com.j0rsa.tutorial.preparedStatement

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.j0rsa.tutorial.preparedStatement.TransactionManager.tx
import org.junit.jupiter.api.Test

internal class QueriesKtTest {

    @Test
    fun testExecSimpleQuery() {
        tx {
            val result =
                """SELECT 'it_works!' v FROM DUAL""".trimIndent()
                    .exec()

            assertThat(result).isNotNull()
            result!!.next()
            assertThat(result.getString("v")).isEqualTo("it_works!")
        }
    }
}