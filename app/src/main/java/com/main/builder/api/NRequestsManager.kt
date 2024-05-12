package com.main.builder.api

import android.content.Context
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class NRequestsManager(
    private val applicationContext: Context
) {
    companion object {
        private fun createReader(applicationContext: Context): BufferedReader {
            return try {
                val f = File(applicationContext.getExternalFilesDir("user_requests"), "n_requests.txt");
                if (!f.exists()) {
                    f.createNewFile();
                }
                BufferedReader(FileReader(f));
            } catch (_: IOException) {
                throw Exception("Impossible to build the reader");
            }
        }
        private fun createWriter(applicationContext: Context): BufferedWriter {
            return try {
                val f = File(applicationContext.getExternalFilesDir("user_requests"), "n_requests.txt");
                if (!f.exists()) {
                    f.createNewFile();
                }
                BufferedWriter(FileWriter(f));
            } catch (_: IOException) {
                throw Exception("Impossible to build the reader");
            }
        }
    }

    fun addOne(): String {
        val inp = createReader(this.applicationContext);
        val content = inp.readText();
        return if (content.isNullOrEmpty()) {
            val out = createWriter(applicationContext);
            out.write("1"); out.flush(); out.close();
            "Success with blank sheet"
        } else {
            val n = Integer.parseInt(content);
            val out = createWriter(applicationContext);
            out.write((n + 1).toString()); out.flush(); out.close();
            "Success with not blank sheet"
        }
    }

    fun getNumber(): String {
        return try {
            val inp = createReader(applicationContext)
            val res = inp.readText();
            inp.close();
            if (res.isNullOrEmpty()) {
                return "0"
            }
            res;
        } catch (e: Exception) {
            e.message.toString()
        }
    }
}