package com.builder.sql

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException

class Finder(
    private val url: String,
    private val username: String,
    private val psw: String,
    private var conn: Connection?,
    private var select: String,
    private var from: String,
    private var where: String
) {
    constructor(): this(
        "jdbc:postgresql://localhost:5432/UniPlan",
        "postgres",
        "5640",
        null,
        "SELECT ?\n",
        "FROM ?\n",
        "WHERE ?\n"
    );

    constructor(url: String, username: String, psw: String): this(
        url,
        username,
        psw,
        null,
        "SELECT ?\n",
        "FROM ?\n",
        "WHERE ?\n"
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

    fun setQuery(select: String, from: String, where: String) {
        this.select = this.select.replace("?", select);
        this.from = this.from.replace("?", from);
        this.where = this.where.replace("?", where);
    }

    fun renewQuery() {
        this.select = "SELECT ?\n";
        this.from = "FROM ?\n";
        this.where = "WHERE ?\n";
    }

    fun getQuery(): String {
        return select + from + where;
    }

    fun execQuery(): ResultSet? {
        return try {
            val st = conn?.prepareStatement(select + from + where);
            st?.executeQuery();
        } catch (e: SQLException) {
            null;
        }
    }



}