package com.main.builder.generic

import android.content.Context
import com.builder.sql.Finder
import com.builder.sql.Inserter
import io.github.cdimascio.dotenv.Dotenv
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date
import java.util.Properties

open class UserBuilder(
    private val applicationcontext: Context,
    private val inserter: Inserter?,
    private val finder: Finder?
) {

    companion object {
        const val INSERT: String = "insert";
        const val FIND: String = "find";

        private fun getSettings(applicationContext: Context): Array<String> {
            val res = Array<String>(3) {""};
            return try {
                val config = applicationContext.assets.open("database.env")
                val properties = Properties()
                properties.load(config)
                config.close()
                res[0] = properties.getProperty("URL");
                res[1] = properties.getProperty("USER");
                res[2] = properties.getProperty("PASSWORD");
                res;
            } catch (e: IOException) {
                e.printStackTrace();
                res;
            }
        }

        private fun iConstructor(applicationcontext: Context): UserBuilder {
            val settings = getSettings(applicationcontext);
            return UserBuilder(applicationcontext, Inserter(settings[0], settings[1], settings[2]), null);
        }

        private fun fConstructor(applicationcontext: Context): UserBuilder {
            val settings = getSettings(applicationcontext);
            return UserBuilder(applicationcontext, null, Finder(settings[0], settings[1], settings[2]));
        }

        fun build(applicationcontext: Context, option: String): UserBuilder? {
            return when (option) {
                INSERT -> iConstructor(applicationcontext);
                FIND -> fConstructor(applicationcontext);
                else -> null;
            }
        }

        private val KEY = "aulicom";
    }

    private fun prettyDate(date: Date): String {
        return "${date.year}-${date.month}-${date.day}"
    }

    private fun encrypt(plainText: String): String {
        val result = StringBuilder()
        val upperCasePlainText = plainText.toUpperCase()
        val upperCaseKey = KEY.toUpperCase()
        val keyLength = KEY.length
        var keyIndex = 0

        for (i in plainText.indices) {
            val currentChar = upperCasePlainText[i]
            if (currentChar.isLetter()) {
                val base = if (currentChar.isLowerCase()) 'a' else 'A'
                val offset = upperCaseKey[keyIndex].toInt() - 'A'.toInt()
                val encryptedChar = (((currentChar.toInt() - base.toInt()) + offset) % 26 + base.toInt()).toChar()
                result.append(encryptedChar)
                keyIndex = (keyIndex + 1) % keyLength
            } else {
                result.append(currentChar)
            }
        }

        return result.toString()
    }

    private fun decrypt(cipherText: String): String {
        val result = StringBuilder()
        val upperCaseCipherText = cipherText.toUpperCase()
        val upperCaseKey = KEY.toUpperCase()
        val keyLength = KEY.length
        var keyIndex = 0

        for (i in cipherText.indices) {
            val currentChar = upperCaseCipherText[i]
            if (currentChar.isLetter()) {
                val base = if (currentChar.isLowerCase()) 'a' else 'A'
                val offset = upperCaseKey[keyIndex].toInt() - 'A'.toInt()
                val decryptedChar = (((currentChar.toInt() - base.toInt()) - offset + 26) % 26 + base.toInt()).toChar()
                result.append(decryptedChar)
                keyIndex = (keyIndex + 1) % keyLength
            } else {
                result.append(currentChar)
            }
        }

        return result.toString()
    }

    fun newUser(infList: Array<String>) {
        val t = Thread {
            inserter?.insertNewUser(infList[0], infList[1], infList[2], prettyDate(Date()), encrypt(infList[3]), infList[4]);
            inserter?.executeQuery();
        }
        t.start();
        val f = File(applicationcontext.filesDir, "settings.env");
        if (f.exists()) {
            var content = f.readText();
            if(content.contains("USERNAME=")) {
                content = content.replace("USERNAME=", "USERNAME=${infList[0]}");
            } else {
                content += "\nUSERNAME=${infList[0]}"
            }
            if(content.contains("PASSWORD=")) {
                content = content.replace("PASSWORD=", "PASSWORD=${infList[4]}");
            } else {
                content += "\nPASSWORD=${infList[4]}"
            }
            val out = BufferedWriter(FileWriter(f))
            out.write(content);
            out.flush();
            out.close();
        }
    }

    fun getUser(username: String, password: String): Array<String> {
        val res: Array<String> = Array(5) {""};
        finder?.renewQuery();
        finder?.setQuery("*", "users", "userid = '$username' AND password = '${encrypt(password)}'");
        val resSet = finder?.execQuery();
        if (resSet != null) {
            while (resSet.next()) {
                res[0] = resSet.getNString("userid");
                res[1] = resSet.getNString("name");
                res[2] = resSet.getNString("surname");
                res[3] = resSet.getNString("password");
                res[4] = resSet.getNString("mail");
            }
        }
        return res;
    }

    fun updateLog(username: String, date: Date) {
        inserter?.updateLastLog("users", username, prettyDate(date));
        inserter?.executeQuery();
    }

    fun getLast_log(username: String): String {
        var res = "";
        finder?.setQuery("last_log", "users", "userid = '$username'");
        val resSet = finder?.execQuery()
        if (resSet != null) {
            while (resSet.next()) {
                res = resSet.getDate("last_log").toString();
            }
        }
        return res;
    }

    private fun localLogExists(): Boolean {
        val f = File(applicationcontext.filesDir, "settings.env");
        if (f.exists()) {
            val reader = Dotenv.configure().directory(f.parent).load();
            return (reader["USERNAME"] != "");
        }
        throw IOException("No settings file")
    }


    fun localLog(): Array<String> {
        if (localLogExists()) {
            val res = Array<String>(2) { "" };
            val f = File(applicationcontext.filesDir, "settings.env");
            if (f.exists()) {
                val reader = Dotenv.configure().directory(f.parent).load();
                res[0] = reader["USERNAME"];
                res[1] = reader["PASSWORD"];
                return res;
            }
        }
        throw IOException("No settings file");
    }

}