package com.j0rsa.tutorial.preparedStatement

import com.j0rsa.tutorial.preparedStatement.TransactionManager.currentTransaction
import org.joda.time.DateTime
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.ArrayList
import kotlin.reflect.KClass

fun String.exec(): ResultSet? =
    with(currentTransaction().connection.prepareStatement(this)) {
        this.executeQuery()
    }

inline fun <reified T : Any> ResultSet?.getValue(name: String): ArrayList<T> =
    this.map { it.toValue(T::class, name) }

@Suppress("UNCHECKED_CAST")
fun <T : Any> ResultSet.toValue(kClass: KClass<T>, name: String): T = this.extractValue(kClass, name) as T

fun <T> ResultSet?.map(transform: (ResultSet) -> T): ArrayList<T> {
    val result = arrayListOf<T>()
    this?.use {
        while (it.next()) {
            result += transform(this)
        }
    }
    return result
}

@Suppress("IMPLICIT_CAST_TO_ANY")
private fun <T : Any> ResultSet.extractValue(property: KClass<T>, name: String) = when (property) {
    DateTime::class -> DateTime(getTimestamp(name))
    BigDecimal::class -> getBigDecimal(name)
    Boolean::class -> getBoolean(name)
    Double::class -> getDouble(name)
    Float::class -> getFloat(name)
    Int::class -> getInt(name)
    Short::class -> getShort(name)
    Long::class -> getLong(name)
    String::class -> getString(name)
    else -> getObject(name, property.java)
}