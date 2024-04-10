package com.main.builder.api
import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import java.util.Date

class RequestBuilder(
    private val applicationContext: Context,
    private val userRequest: String,
    private val examDate: String,
    private val examSubject: String,
) {

    companion object {
        private fun prettyDate(d: Date): String {
            return "${d.day}-${d.month}-${d.year + 1900}";
        }
    }

    constructor(applicationContext: Context, examSubject: String, examDate: String): this(
        applicationContext,
        Json.encodeToString("I have an exam of $examSubject in date $examDate, today is ${prettyDate(Date())} that contains those arguments:\n<EXAM_ARGUMENT_LIST>\nWrite me a detailed study schedule program that evenly distributes the study across the days I have. Write the response in JSON format, using the date with day and month as the key, and the corresponding topic for that date as the value."),
        examDate,
        examSubject
    )


    fun build(topics: MutableList<String>): String {
        fun listToString(topics: MutableList<String>): String {
            var res = "";
            var index = 0;
            for (topic in topics) {
                if (index == (topics.size - 1)) {
                    res += topic;
                } else {
                    res += "$topic, ";
                    index++;
                }
            }
            return res;
        }
        return userRequest.replace("<EXAM_ARGUMENT_LIST>", listToString(topics));
    }

    fun buildFile() {
        try {
            File(applicationContext.filesDir, "$examSubject-$examDate").createNewFile();
        } catch (_: IOException) { }
    }
}