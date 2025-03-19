package effective.interview.revolut

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    Database.connect(
        url = "jdbc:h2:mem:test",
        driver = "org.h2.Driver",
        user = "root",
        password = ""
    )

    // Create table
    transaction {
        exec("CREATE TABLE IF NOT EXISTS USERS (id INT PRIMARY KEY, name VARCHAR(50))")
    }

    // Insert data
    transaction {
        exec("INSERT INTO USERS (id, name) VALUES (1, 'John')")
        exec("INSERT INTO USERS (id, name) VALUES (2, 'Jane')")
    }

    transaction {
        val users = exec("SELECT * FROM users") { resultSet ->
            resultSet.getString("name") // Extract the "name" column
        }
        users?.forEach {
            println("User: ${it}")
        }
    }
}