package com.j0rsa.tutorial.preparedStatement

import com.j0rsa.tutorial.preparedStatement.TransactionManager.currentTransaction
import java.sql.ResultSet

fun String.exec(): ResultSet? =
    with(currentTransaction().connection.prepareStatement(this)) {
        this.executeQuery()
    }