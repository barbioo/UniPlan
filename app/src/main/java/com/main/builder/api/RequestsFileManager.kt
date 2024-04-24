package com.builder.api

import android.content.Context
import android.util.MutableBoolean
import okhttp3.internal.toImmutableList
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException


class RequestsFileManager(
    private val inp: BufferedReader,
    private val out: BufferedWriter
) {
    companion object {
        private fun createReader(applicationContext: Context): BufferedReader {
             try {
                 val f = File(applicationContext.getExternalFilesDir("user_requests"), "user_requests.txt")
                 if (!f.exists()) {
                     f.createNewFile();
                 }
                 return BufferedReader(FileReader(f));
             } catch (e: IOException) {
                 throw IOException("Something went wrong whit reader creation: ${e.stackTrace}")
             }
        }

        private fun createWriter(applicationContext: Context): BufferedWriter {
            try {
                val f = File(applicationContext.getExternalFilesDir("user_requests"), "user_requests.txt")
                if (!f.exists()) {
                    f.createNewFile();
                }
                return BufferedWriter(FileWriter(f, true) );
            } catch (e: IOException) {
                throw IOException("Something went wrong whit writer creation: ${e.stackTrace}")
            }
        }

        fun extractSubFromFileName(fileName: String): String {
            return fileName.split("-")[0];
        }

    }

    constructor(applicationContext: Context): this (
        createReader(applicationContext),
        createWriter(applicationContext)
    )

    fun addRequestToList(examSubject: String, examDate: String): String {
        return try {
            val content = inp.readText();
            if (!content.contains("$examSubject-$examDate.json")) {
                out.write("$examSubject-$examDate.json\n")
                out.flush()
                out.close()
            }
            "Success"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    fun getFilesNames(): MutableList<String> {
        return try {
            val content = inp.readText()
            val res = content.split("\n")
            val fileList = mutableListOf<String>()
            res.forEach { line ->
                if (line != "") {
                    fileList.add(line)
                }
            }
            fileList
        } catch (e: Exception) {
            throw Exception(e.message.toString())
        }
    }

}