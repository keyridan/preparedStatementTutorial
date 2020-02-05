package com.j0rsa.tutorial.preparedStatement

import com.j0rsa.tutorial.preparedStatement.TransactionManager.currentTransaction
import org.joda.time.DateTime
import java.math.BigDecimal
import java.sql.ResultSet
import java.util.*
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

fun <T : Any> ResultSet.toDataClass(kClass: KClass<T>): T {
    val ctor = kClass.primaryConstructor!!
    val properties = ctor.parameters
    val values = properties.map { this.extractValue(it.type.jvmErasure, it.name!!) }
    return ctor.call(*values.toTypedArray())
}

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

sealed class ResultColumn<T> {
    data class BooleanColumn(val name: String) : ResultColumn<Boolean>()
    data class IntColumn(val name: String) : ResultColumn<Int>()
    data class StringColumn(val name: String) : ResultColumn<String>()
    data class UUIDColumn(val name: String) : ResultColumn<UUID>()
    data class DoubleColumn(val name: String) : ResultColumn<Double>()
    data class FloatColumn(val name: String) : ResultColumn<Float>()
    data class LongColumn(val name: String) : ResultColumn<Long>()
    data class ShortColumn(val name: String) : ResultColumn<Short>()
    data class BigDecimalColumn(val name: String) : ResultColumn<BigDecimal>()
    data class DateTimeColumn(val name: String) : ResultColumn<DateTime>()
}

@Suppress("IMPLICIT_CAST_TO_ANY")
inline infix fun <reified T> ResultColumn<T>.from(res: ResultSet) =
    when (this) {
        is ResultColumn.BooleanColumn -> res.getBoolean(name)
        is ResultColumn.IntColumn -> res.getInt(name)
        is ResultColumn.StringColumn -> res.getString(name)
        is ResultColumn.UUIDColumn -> res.getObject(name, UUID::class.java)
        is ResultColumn.DoubleColumn -> res.getDouble(name)
        is ResultColumn.FloatColumn -> res.getFloat(name)
        is ResultColumn.LongColumn -> res.getLong(name)
        is ResultColumn.ShortColumn -> res.getShort(name)
        is ResultColumn.BigDecimalColumn -> res.getBigDecimal(name)
        is ResultColumn.DateTimeColumn -> DateTime(res.getTimestamp(name))
    } as T