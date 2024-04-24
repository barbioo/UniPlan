package com.builder.api

import android.content.Context
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ResponseWriter(
    private val applicationContext: Context,
    private var out: BufferedWriter,
    ) {

    companion object {
        private fun createWriter(applicationContext: Context, examSubject: String, examDate: String): BufferedWriter {
            val f = File(applicationContext.getExternalFilesDir("data_responses"), "$examSubject-$examDate.json");
            return if (f.exists()) {
                BufferedWriter(FileWriter(f))
            } else {
                throw IOException("No such file")
            }
        }
    }

    constructor(applicationContext: Context, examSubject: String, examDate: String) : this(
        applicationContext,
        createWriter(applicationContext, examSubject, examDate)
    )

    fun printResponse(json: String): String {
        return try {
            out.write(json);
            out.flush();
            out.close();
            "OK"
        } catch (e: IOException) {
            e.message.toString()
        }
    }
}