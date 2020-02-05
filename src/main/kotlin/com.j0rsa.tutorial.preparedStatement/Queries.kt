package com.j0rsa.tutorial.preparedStatement

import com.j0rsa.tutorial.preparedStatement.TransactionManager.currentTransaction
import java.sql.ResultSet
import java.util.ArrayList
import kotlin.reflect.KClass

fun String.exec(): ResultSet? =
    with(currentTransaction().connection.prepareStatement(this)) {
        this.executeQuery()
    }