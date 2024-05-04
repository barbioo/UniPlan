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
    private var done: Boolean
) : Serializable {

    constructor() : this("default", "1-1-2024", "default", false)

    fun getSubject(): String {
        // Restituisci il valore della proprietà 'subject'
        return subject
    }

    fun getDone(): Boolean {
        return done
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

    fun setDone(done: Boolean) {
        this.done = done
    }

    override fun toString(): String {
        return "Occurrence(subject='$subject', date=$date, topic='$topic', done='$done')"
    }


}