package com.builder.api

import android.content.Context
import android.nfc.FormatException
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate

class RequestBuilder(
    private val applicationContext: Context,
    private val userRequest: String,
    private val examDate: String,
    private val examSubject: String,
) {

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private fun todayDate(): String {
            try {
                val d = LocalDate.now();
                return "${d.dayOfMonth}-${d.monthValue}-${d.year}"
            } catch (e: Exception) {
                throw FormatException("Impossible to access todays date");
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(applicationContext: Context, examSubject: String, examDate: String): this(
        applicationContext,
        Json.encodeToString("I have an exam of ${examSubject.replace("-", " ")} in date $examDate, today is ${todayDate()} that contains those arguments:\n<EXAM_ARGUMENT_LIST>\nWrite me a detailed study schedule program that evenly distributes the study across the days I have. Write the response in JSON format, using the date with day and month as the key, and the corresponding topic for that date as the value."),
        examDate,
        examSubject.replace("-", " ")
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

    fun buildFile(): String {
        return try {
            val root = File(applicationContext.getExternalFilesDir("data_responses"), "$examSubject-$examDate.json");
            if (root.exists()) {
                "Exists"
            } else {
                root.createNewFile().toString()
            }
        } catch (e: Exception) {
            e.message.toString()
        }
    }

}