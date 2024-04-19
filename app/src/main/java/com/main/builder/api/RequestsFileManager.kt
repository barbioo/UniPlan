package com.main.builder.api

import android.content.Context
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

    fun getFileName(examSubject: String, examDate: String): String {
        return try {
            val content = inp.readText();
            if (content.contains("$examSubject-$examDate.json")) {
                "$examSubject-$examDate.json"
            } else {
                "$examSubject-$examDate wasn't send as a request"
            }
        } catch (e: Exception) {
            e.message.toString()
        }
    }

}