package com.builder.api

import android.content.Context
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ResponseWriter(
    private val applicationContext: Context,
    private var out: BufferedWriter,
    ) {

    constructor(applicationContext: Context, examSubject: String, examDate: String) : this(
        applicationContext,
        BufferedWriter(FileWriter(File(applicationContext.getExternalFilesDir("data_responses"), "$examSubject-$examDate")))
    )

    fun printResponse(json: String): Boolean {
        return try {
            out.write(json);
            out.close();
            true;
        } catch (_: IOException) {
            false;
        }
    }
}