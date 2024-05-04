package com.main.builder.api

import android.content.Context
import com.objects.Subject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class ResponseWriter(
    private val applicationContext: Context,
    private var out: BufferedWriter,
    ) {

    companion object {
        private fun createWriter(applicationContext: Context, examSubject: String, requestDate: String, examDate: String): BufferedWriter {
            val f = File(applicationContext.getExternalFilesDir("data_responses"), "$examSubject-$requestDate-$examDate.json");
            return if (f.exists()) {
                BufferedWriter(FileWriter(f))
            } else {
                throw IOException("No such file")
            }
        }
    }

    constructor(applicationContext: Context, examSubject: String, requestDate: String, examDate: String) : this(
        applicationContext,
        createWriter(applicationContext, examSubject, requestDate, examDate)
    )

    constructor(applicationContext: Context, sub: Subject): this(
        applicationContext,
        createWriter(applicationContext, sub.getSubject(), sub.getRequestDate(), sub.getExamDate())
    )

    fun printJson(json: String): String {
        return try {
            out.write(json);
            out.flush();
            out.close();
            "OK"
        } catch (e: IOException) {
            e.message.toString()
        }
    }

    fun renewWriter(applicationContext: Context, sub: Subject) {
        this.out = createWriter(applicationContext, sub.getSubject(), sub.getRequestDate(), sub.getExamDate());
    }
}