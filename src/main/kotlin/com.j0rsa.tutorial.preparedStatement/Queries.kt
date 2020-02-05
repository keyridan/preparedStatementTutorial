package com.j0rsa.tutorial.preparedStatement

import com.j0rsa.tutorial.preparedStatement.TransactionManager.currentTransaction
import java.sql.ResultSet
import java.util.ArrayList

fun String.exec(): ResultSet? =
    with(currentTransaction().connection.prepareStatement(this)) {
        this.executeQuery()
    }

fun ResultSet?.getValue(name: String): ArrayList<String> = this.map { it.toValue(name) }

fun ResultSet.toValue(name: String): String = this.getString(name)

fun <T> ResultSet?.map(transformer: (ResultSet) -> T): ArrayList<T> {
    val result = arrayListOf<T>()
    this?.use {
        while (it.next()) {
            result += transformer(this)
        }
    }
    return result
}