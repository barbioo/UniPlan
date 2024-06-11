package com.objects

import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.time.LocalDate

class Subject(
    private var subject: String,
    private var examDate: String,
    private var requestDate: String,
    private val name: String,
    private val date: String
) : Serializable {

    companion object {
        fun deserialize(json: String): Subject {
            return try {
                val res = Subject();
                val tmp = JSONObject(json);
                res.subject = tmp.getString("subject");
                res.examDate = tmp.getString("examDate");
                res.requestDate = tmp.getString("requestDate");
                res;
            } catch (_: JSONException) {
                throw Exception("Bad serialization of ${json}")
            }
        }
    }

    constructor() : this(
        "default",
        "02-01-2000",
        "01-01-2000",
        "default",
        "01-01-2000"
    )



    constructor(fileName: String): this() {
        /*
        val regex = Regex("\\d+")

        val nFileName = fileName.replace(".json", "");
        val match = regex.find(nFileName)
        match?.let {
            val index = it.range.first
            subject = fileName.substring(0, index).trim().replace("-", "")
            examDate = fileName.substring(index).trim()
        }
        */
        val tmp = fileName.replace(".json", "").split("-");
        this.subject = tmp.first();
        this.requestDate = "${tmp[1]}-${tmp[2]}-${tmp[3]}";
        this.examDate = "${tmp[4]}-${tmp[5]}-${tmp[6]}";
    }

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(subject: String, examDate: String): this() {
        @RequiresApi(Build.VERSION_CODES.O)
        fun todayDate(): String {
            return try {
                val tmp = LocalDate.now()
                "${tmp.dayOfMonth}-${tmp.month.value}-${tmp.year}"
            } catch (e: Exception) {
                e.message.toString()
            }
        }

        this.subject = subject;
        this.requestDate = todayDate();
        this.examDate = examDate;
    }


    fun setSubject(v: String) {
        subject = v
    }

    fun setExamDate(v: String) {
        examDate = v
    }

    fun setRequestDate(v: String) {
        requestDate = v;
    }

    fun getSubject(): String {
        return subject
    }

    fun getRequestDate(): String {
        return requestDate;
    }

    fun getExamDate(): String {
        return examDate
    }

    override fun toString(): String {
        return "Subject(subject='$subject', requestDate='$requestDate', examDate='$examDate')"
    }

    fun serialize(): String {
        return try {
            val res = JSONObject();
            res.put("subject", this.subject);
            res.put("requestDate", this.requestDate);
            res.put("examDate", this.examDate);
            res.toString();
        } catch (_: JSONException) {
            throw Exception("Bad serialization of ${this}")
        }
    }

    private val topics = mutableListOf<String>()
    fun getName(): String {
        return name
    }

    fun getDate(): String {
        return date
    }

    fun addTopic(topic: String) {
        topics.add(topic)
    }

    fun getTopics(): List<String> {
        return topics
    }


}