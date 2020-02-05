package com.j0rsa.tutorial.preparedStatement

import com.j0rsa.tutorial.preparedStatement.TransactionManager.currentTransaction
import org.joda.time.DateTime
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.ArrayList
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

fun String.exec(): ResultSet? =
    with(currentTransaction().connection.prepareStatement(this)) {
        this.executeQuery()
    }

inline fun <reified T : Any> ResultSet?.toEntities(): ArrayList<T> =
    this.map { it.toEntity<T>() }

inline fun <reified T : Any> ResultSet.toEntity(): T = this.toDataClass(T::class)

inline fun <reified T : Any> ResultSet?.getValue(name: String): ArrayList<T> =
    this.map { it.toValue(T::class, name) }

fun <T : Any> ResultSet.toDataClass(kClass: KClass<T>): T {
    val ctor = kClass.primaryConstructor!!
    val properties = ctor.parameters
    val values = properties.map { this.extractValue(it.type.jvmErasure, it.name!!) }
    return ctor.call(*values.toTypedArray())
}

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