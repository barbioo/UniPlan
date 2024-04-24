package com.objects

import java.io.Serializable

class Subject(
    private var subject: String,
    private var examDate: String
) : Serializable {

    constructor() : this(
        "default",
        "01-01-2000"
    )

    constructor(fileName: String): this() {
        val regex = Regex("\\d+")
        val match = regex.find(fileName)
        match?.let {
            val index = it.range.first
            subject = fileName.substring(0, index).trim().replace("-", "")
            examDate = fileName.substring(index).trim().replace(".json", "")
        }
    }

    fun setSubject(v: String) {
        subject = v
    }

    fun setExamDate(v: String) {
        examDate = v
    }

    fun getSubject() {
        subject
    }

    fun getExamDate() {
        examDate
    }

    override fun toString(): String {
        return "Subject(subject='$subject', examDate='$examDate')"
    }

}