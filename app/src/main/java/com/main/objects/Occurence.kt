package objects;

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.CalendarContract
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class Occurrence(
    private var subject: String,
    private var date: String,
    private var topic: String,
    private var user: String
) : Serializable {

    constructor() : this("default", "1-1-2024", "default", "default")

    fun getSubject(): String {
        // Restituisci il valore della proprietà 'subject'
        return subject
    }

    // Metodo getter per la proprietà 'date'
    fun getDate(): String {
        // Restituisci il valore della proprietà 'date'
        return date
    }

    // Metodo getter per la proprietà 'topic'
    fun getTopic(): String {
        // Restituisci il valore della proprietà 'topic'
        return topic
    }

    // Metodo getter per la proprietà 'user'
    fun getUser(): String {
        // Restituisci il valore della proprietà 'user'
        return user
    }

    // Setter for subject
    fun setSubject(subject: String) {
        this.subject = subject
    }

    // Setter for date
    fun setDate(date: String) {
        this.date = date
    }

    // Setter for topic
    fun setTopic(topic: String) {
        this.topic = topic
    }

    // Setter for user
    fun setUser(user: String) {
        this.user = user
    }

    override fun toString(): String {
        return "Occurrence(subject='$subject', date=$date, topic='$topic', user='$user')"
    }


}