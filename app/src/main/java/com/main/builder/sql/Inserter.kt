package com.builder.sql

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

class Inserter(
    private val url: String,
    private val username: String,
    private val psw: String,
    private var conn: Connection?,
    private var query: String
) {

    constructor(): this(
        "jdbc:postgresql://localhost:5432/UniPlan",
        "postgres",
        "5640",
        null,
        ""
    );

    constructor(url: String, username: String, psw: String): this(
        url,
        username,
        psw,
        null,
        ""
    );

    fun connect() {
        conn = DriverManager.getConnection(url, username, psw);
    }

    fun disconnect() {
        try {
            conn?.close();
        } catch (e: SQLException) {
            conn?.close();
        }
    }

    init {
        try {
            connect()
        } catch (e: SQLException) {
            connect()
        }
    }

    fun getQuery(): String {
        return query;
    }

    fun executeQuery(): ResultSet? {
        val ps = this.conn?.prepareStatement(this.query);
        return ps?.executeQuery();
    }

    fun updateLastLog(tableName: String, userid: String, date: String) {
        this.query = "UPDATE $tableName\nSET last_log = '$date' WHERE userid  = $userid";
    }

    fun insertNewUser(userid: String, name: String, surname: String, last_log: String, password: String) {
        this.query = "INSERT INTO users (userid, name, surname, last_log, password)";
        this.query += "($userid, $name, $surname, $last_log, $password)";
    }

}